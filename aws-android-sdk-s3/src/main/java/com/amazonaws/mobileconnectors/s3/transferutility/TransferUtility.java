/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.mobileconnectors.s3.transferutility;

import static com.amazonaws.services.s3.internal.Constants.MAXIMUM_UPLOAD_PARTS;
import static com.amazonaws.services.s3.internal.Constants.MB;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.VersionInfoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The transfer utility is a high-level class for applications to upload and
 * download files. It inserts upload and download records into the database and
 * starts a Service to execute the tasks in the background. Here is the usage:
 *
 * <pre>
 * // Initializes TransferUtility
 * TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
 * // Starts a download
 * TransferObserver observer = transferUtility.download(&quot;bucket_name&quot;, &quot;key&quot;, file);
 * observer.setTransferListener(new TransferListener() {
 *     public void onStateChanged(int id, String newState) {
 *         // Do something in the callback.
 *     }
 *
 *     public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
 *         // Do something in the callback.
 *     }
 *
 *     public void onError(int id, Exception e) {
 *         // Do something in the callback.
 *     }
 * });
 * </pre>
 *
 * For pausing and resuming tasks:
 *
 * <pre>
 * // Gets id of the transfer.
 * int id = observer.getId();
 *
 * // Pauses the transfer.
 * transferUtility.pause(id);
 *
 * // Resumes the transfer.
 * transferUtility.resume(id);
 * </pre>
 *
 * For cancelling and deleting tasks:
 *
 * <pre>
 * // Cancels the transfer.
 * transferUtility.cancel(id);
 *
 * // Deletes the transfer.
 * transferUtility.delete(id);
 * </pre>
 *
 * Note that the Activity or Service that instantiates and uses the
 * TransferUtility should keep a reference to the Amazon S3 client as a class
 * attribute. Failing to keep a reference may cause the TransferService to fail
 * since {@link TransferService} receives a weak reference to the AmazonS3
 * client allowing it to be garbage collected.
 */
public class TransferUtility {

    private static final String TAG = "TransferUtility";

    /**
     * Default minimum part size for upload parts. Anything below this will use
     * a single upload
     */
    static final int MINIMUM_UPLOAD_PART_SIZE = 5 * MB;

    private final Context appContext;
    private final String s3WeakReferenceMapKey;
    private final TransferDBUtil dbUtil;

    /**
     * Constructs a new TransferUtility specifying the client to use and
     * initializes configuration of TransferUtility and a key for S3 client weak
     * reference.
     *
     * @param s3 The client to use when making requests to Amazon S3
     * @param context The current context
     * @param configuration Configuration parameters for this TransferUtility
     */
    public TransferUtility(AmazonS3 s3, Context context) {
        this.s3WeakReferenceMapKey = UUID.randomUUID().toString();
        S3ClientWeakReference.put(s3WeakReferenceMapKey, s3);
        this.appContext = context.getApplicationContext();
        this.dbUtil = new TransferDBUtil(appContext);
    }

    /**
     * Starts downloading the S3 object specified by the bucket and the key to
     * the given file. The file must be a valid file. Directory isn't supported.
     * Note that if the given file exists, it'll be overwritten.
     *
     * @param bucket The name of the bucket containing the object to download.
     * @param key The key under which the object to download is stored.
     * @param file The file to download the object's data to.
     * @return A TransferObserver used to track download progress and state
     */
    public TransferObserver download(String bucket, String key, File file) {
        if (file == null || file.isDirectory()) {
            throw new IllegalArgumentException("Invalid file: " + file);
        }
        Uri uri = dbUtil.insertSingleTransferRecord(TransferType.DOWNLOAD,
                bucket, key, file);
        int recordId = Integer.parseInt(uri.getLastPathSegment());
        if (file.isFile()) {
            Log.w(TAG, "Overwrite existing file: " + file);
            file.delete();
        }

        sendIntent(TransferService.INTENT_ACTION_TRANSFER_ADD, recordId);
        return new TransferObserver(recordId, dbUtil, file);
    }

    /**
     * Starts uploading the file to the given bucket, using the given key. The
     * file must be a valid file. Directory isn't supported.
     *
     * @param bucket The name of the bucket to upload the new object to.
     * @param key The key in the specified bucket by which to store the new
     *            object.
     * @param file The file to upload.
     * @return A TransferObserver used to track upload progress and state
     */
    public TransferObserver upload(String bucket, String key, File file) {

        return upload(bucket, key, file, new ObjectMetadata());
    }

    /**
     * Starts uploading the file to the given bucket, using the given key. The
     * file must be a valid file. Directory isn't supported.
     *
     * @param bucket The name of the bucket to upload the new object to.
     * @param key The key in the specified bucket by which to store the new
     *            object.
     * @param file The file to upload.
     * @param metadata The S3 metadata to associate with this object
     * @return A TransferObserver used to track upload progress and state
     */
    public TransferObserver upload(String bucket, String key, File file, ObjectMetadata metadata) {
        if (file == null || file.isDirectory()) {
            throw new IllegalArgumentException("Invalid file: " + file);
        }
        int recordId = 0;
        if (shouldUploadInMultipart(file)) {
            recordId = createMultipartUploadRecords(bucket, key, file, metadata);
        } else {

            Uri uri = dbUtil.insertSingleTransferRecord(TransferType.UPLOAD,
                    bucket, key, file, metadata);
            recordId = Integer.parseInt(uri.getLastPathSegment());
        }

        sendIntent(TransferService.INTENT_ACTION_TRANSFER_ADD, recordId);
        return new TransferObserver(recordId, dbUtil, file);
    }

    /**
     * Gets a TransferObserver instance to track the record with the given id.
     *
     * @param id A transfer id.
     * @return The TransferObserver instance which is observing the record.
     */
    public TransferObserver getTransferById(int id) {
        Cursor c = dbUtil.queryTransferById(id);
        try {
            if (c.moveToFirst()) {
                return new TransferObserver(id, dbUtil, c);
            } else {
                return null;
            }
        } finally {
            c.close();
        }
    }

    /**
     * Gets a list of TransferObserver instances which are observing records
     * with the given type.
     *
     * @param type The type of the transfer "any".
     * @return A list of TransferObserver instances.
     */
    public List<TransferObserver> getTransfersWithType(TransferType type) {
        List<TransferObserver> transferObservers = new ArrayList<TransferObserver>();
        Cursor c = dbUtil.queryAllTransfersWithType(type);
        try {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow(TransferTable.COLUMN_ID));
                transferObservers.add(new TransferObserver(id, dbUtil, c));
            }
        } finally {
            c.close();
        }
        return transferObservers;
    }

    /**
     * Gets a list of TransferObserver instances which are observing records
     * with the given type.
     *
     * @param type The type of the transfer.
     * @param state The state of the transfer.
     * @return A list of TransferObserver of transfer records with the given
     *         type and state.
     */
    public List<TransferObserver> getTransfersWithTypeAndState(TransferType type,
            TransferState state) {
        List<TransferObserver> transferObservers = new ArrayList<TransferObserver>();
        Cursor c = dbUtil.queryTransfersWithTypeAndState(type, state);
        try {
            while (c.moveToNext()) {
                int partNum = c.getInt(c.getColumnIndexOrThrow(TransferTable.COLUMN_PART_NUM));
                if (partNum != 0) {
                    // skip parts of a multipart upload
                    continue;
                }
                int id = c.getInt(c.getColumnIndexOrThrow(TransferTable.COLUMN_ID));
                transferObservers.add(new TransferObserver(id, dbUtil, c));
            }
        } finally {
            c.close();
        }
        return transferObservers;
    }

    /**
     * Inserts a multipart summary record and actual part records into database
     *
     * @param bucket The name of the bucket to upload the new object to.
     * @param key The key in the specified bucket by which to store the new
     *            object.
     * @param file The file to upload.
     * @param isUsingEncryption Whether the upload is encrypted.
     * @return Number of records created in database
     */
    private int createMultipartUploadRecords(String bucket, String key, File file,
            ObjectMetadata metadata) {
        long remainingLenth = file.length();
        double partSize = (double) remainingLenth / (double) MAXIMUM_UPLOAD_PARTS;
        partSize = Math.ceil(partSize);
        long optimalPartSize = (long) Math.max(partSize, MINIMUM_UPLOAD_PART_SIZE);
        long fileOffset = 0;
        int partNumber = 1;

        // the number of parts
        int partCount = (int) Math.ceil((double) remainingLenth / (double) optimalPartSize);

        /*
         * the size of valuesArray is partCount + 1, one for a multipart upload
         * summary, others are actual parts to be uploaded
         */
        ContentValues[] valuesArray = new ContentValues[partCount + 1];
        valuesArray[0] = dbUtil.generateContentValuesForMultiPartUpload(bucket, key,
                file, fileOffset, 0, "", file.length(), 0, metadata);
        for (int i = 1; i < partCount + 1; i++) {
            long bytesForPart = Math.min(optimalPartSize, remainingLenth);
            valuesArray[i] = dbUtil.generateContentValuesForMultiPartUpload(bucket, key,
                    file, fileOffset, partNumber, "", bytesForPart, remainingLenth
                            - optimalPartSize <= 0 ? 1 : 0, metadata);
            fileOffset += optimalPartSize;
            remainingLenth -= optimalPartSize;
            partNumber++;
        }
        return dbUtil.bulkInsertTransferRecords(valuesArray);
    }

    /**
     * Pauses a transfer task with the given id.
     *
     * @param id A transfer id specifying the transfer to be paused
     * @return Whether successfully paused
     */
    public boolean pause(int id) {
        sendIntent(TransferService.INTENT_ACTION_TRANSFER_PAUSE, id);
        return true;
    }

    /**
     * Pauses all transfers which have the given type.
     *
     * @param type The type of transfers
     */
    public void pauseAllWithType(TransferType type) {
        Cursor c = dbUtil.queryAllTransfersWithType(type);
        try {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow(TransferTable.COLUMN_ID));
                pause(id);
            }
        } finally {
            c.close();
        }
    }

    /**
     * Resumes the transfer task with the given id. You can resume a transfer in
     * paused, canceled or failed state. If a transfer is in waiting or in
     * progress state but it isn't actually running, this operation will force
     * it to run.
     *
     * @param id A transfer id specifying the transfer to be resumed
     * @return A TransferObserver of the resumed upload/download or null if the
     *         ID does not represent a paused transfer
     */
    public TransferObserver resume(int id) {
        sendIntent(TransferService.INTENT_ACTION_TRANSFER_RESUME, id);
        return getTransferById(id);
    }

    /**
     * Sets a transfer to be canceled. Note the TransferState must be
     * TransferState.CANCELED before the transfer is guaranteed to have stopped,
     * and can be safely deleted
     *
     * @param id A transfer id specifying the transfer to be canceled
     * @return Whether the transfer was set to be canceled.
     */
    public boolean cancel(int id) {
        sendIntent(TransferService.INTENT_ACTION_TRANSFER_CANCEL, id);
        return true;
    }

    /**
     * Sets all transfers which have the given type to be canceled. Note the
     * TransferState must be TransferState.CANCELED before the transfer is
     * guaranteed to have stopped, and can be safely deleted
     *
     * @param type The type of transfers
     */
    public void cancelAllWithType(TransferType type) {
        Cursor c = dbUtil.queryAllTransfersWithType(type);
        try {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow(TransferTable.COLUMN_ID));
                cancel(id);
            }
        } finally {
            c.close();
        }
    }

    /**
     * Deletes a transfer record with the given id. It just deletes the record
     * but does not stop the running thread, so you must cancel the task before
     * deleting the record.
     *
     * @param id A transfer id specifying the transfer to be deleted.
     * @return true if at least one record was deleted
     */
    public boolean deleteTransferRecord(int id) {
        cancel(id);
        return dbUtil.deleteTransferRecords(id) > 0;
    }

    /**
     * Send an intent to {@link TransferService}
     *
     * @param action action to perform
     * @param id id of the transfer
     */
    private void sendIntent(String action, int id) {
        Intent intent = new Intent(appContext, TransferService.class);
        intent.setAction(action);
        intent.putExtra(TransferService.INTENT_BUNDLE_TRANSFER_ID, id);
        intent.putExtra(TransferService.INTENT_BUNDLE_S3_WEAK_REFERENCE_KEY, s3WeakReferenceMapKey);
        appContext.startService(intent);
    }

    private boolean shouldUploadInMultipart(File file) {
        if (file != null
                && file.length() > MINIMUM_UPLOAD_PART_SIZE) {
            return true;
        } else {
            return false;
        }
    }

    static <X extends AmazonWebServiceRequest> X appendTransferServiceUserAgentString(X request) {
        request.getRequestClientOptions().appendUserAgent("TransferService/"
                + VersionInfoUtils.getVersion());
        return request;
    }

    static <X extends AmazonWebServiceRequest> X appendMultipartTransferServiceUserAgentString(
            X request) {
        request.getRequestClientOptions().appendUserAgent("TransferService_multipart/"
                + VersionInfoUtils.getVersion());
        return request;
    }

}
