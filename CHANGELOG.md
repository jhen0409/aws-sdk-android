# Change Log - AWS SDK for Android

## [Release 2.2.12](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.12) (02/12/2016)

### New Features
- **AWS IoT**: [AWS IoT](https://aws.amazon.com/iot/) is now supported by the SDK. AWS IoT is a managed cloud platform that lets connected devices easily and securely interact with cloud applications and other devices. AWS IoT can support billions of devices and trillions of messages, and can process and route those messages to AWS endpoints and to other devices reliably and securely. With AWS IoT, your applications can keep track of and communicate with all your devices, all the time, even when they aren't connected.

### Improvements
- **Amazon Web Services**: General service updates and documentation improvements.


## [Release 2.2.11](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.11) (01/28/2016)

### New Features
- **AWS Key Management Service**: [AWS Key Management Service (KMS)](https://aws.amazon.com/kms/) is now supported by the SDK. AWS Key Management Service (KMS) is a managed service that makes it easy for you to create and control the encryption keys used to encrypt your data, and uses Hardware Security Modules (HSMs) to protect the security of your keys. AWS Key Management Service is integrated with several other AWS services to help you protect your data you store with these services. AWS Key Management Service is also integrated with AWS CloudTrail to provide you with logs of all key usage to help meet your regulatory and compliance needs.

### Improvements
- **Amazon S3**: Revamped S3 TransferUtility. Huge performance boost and lots of enhancements.
 - Offload most database operations to background thread.
 - Re-architected transfer listeners. Moved away from ContentObserver.
 - Reduced the frequency of writing transfer states to database.
 - Better error reporting. Now the original exception is passed to [TransferListener.onError(int, Exception)](http://docs.aws.amazon.com/AWSAndroidSDK/latest/javadoc/com/amazonaws/mobileconnectors/s3/transferutility/TransferListener.html#onError(int,%20java.lang.Exception)). [#61](https://github.com/aws/aws-sdk-android/issues/61)
 - Allow user to resume a transfer in any state other than `TransferState.COMPLETED`. [#81](https://github.com/aws/aws-sdk-android/issues/81) and [#87](https://github.com/aws/aws-sdk-android/issues/87)
 - Better diagnostics of TransferService. You can dump its status with `adb shell dumpsys activity service com.amazonaws.mobileconnectors.s3.transferutility.TransferService`. It works only if the app is debuggable.
 - Better handling of network connectivity changes.
 - Other cleanups, bug fixes and improvements.
- **Amazon S3**: Adds support of server-side encryption with AWS Key Management Service. See [Amazon S3 developer guide](http://docs.aws.amazon.com/AmazonS3/latest/dev/UsingKMSEncryption.html) for more information.
- **Amazon S3**: Signature Version 4 is now the default signing methods for all S3 requests as long as a region is specified or can be easily determined from the given endpoint.


## [Release 2.2.10](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.10) (01/06/2016)

### New Features
- **Amazon Web Services**: Added support for new AWS region in South Korea (ap-northeast-2).
- **Amazon Kinesis Firehose**: [Amazon Kinesis Firehose](https://aws.amazon.com/kinesis/firehose/) is the easiest way to load streaming data into AWS. It can capture and automatically load streaming data into Amazon S3 and Amazon Redshift, enabling near real-time analytics with existing business intelligence tools and dashboards you are already using today. See the [developer guide](http://docs.aws.amazon.com/mobile/sdkforandroid/developerguide/kinesis.html) for instructions about using `KinesisFirehoseRecorder`.

### Improvements
- **Amazon S3**: Allow user to add or overwrite file extension to MIME type mapping so that AmazonS3 can identify the MIME type of consequent uploads and set the content type correctly. [#83](https://github.com/aws/aws-sdk-android/issues/83)
- **AWS Lambda**: Added a method to access the installation id of the client context that is sent to AWS Lambda. [#74](https://github.com/aws/aws-sdk-android/issues/74)
- **Amazon Web Services**: General service updates and documentation improvements.

### Bug Fixes
- **Amazon S3**: Fixed the termination condition of TransferService so that it stays alive when there are active transfers waiting for network. [#70](https://github.com/aws/aws-sdk-android/issues/70)
- **Amazon S3**: Fixed a resource leak issue in TransferUtility caused by unclosed cursor.
- **AWS Lambda**: Fixed a potential issue with Lambda when invoking methods with no args. [#80](https://github.com/aws/aws-sdk-android/pull/80)
- **API Gateway**: Updated the message of ApiClientException to match exactly what the API responds. [#78](https://github.com/aws/aws-sdk-android/pull/78)


## [Release 2.2.9](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.9) (11/18/2015)

### New Features
- **Amazon Web Services**: General service updates and documentation improvements.

### Bug Fixes
- **Amazon S3**: Fixed slow initialization of Amazon S3 client. [#69](https://github.com/aws/aws-sdk-android/issues/69)
- **General**: Updated instruction for proguard.

## [Release 2.2.8](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.8) (11/05/2015)

### New Features
- **Amazon Web Services**: General service updates and documentation improvements.

### Bug Fixes
- **AWS Core Runtime Library**: Improved retry logic so that aborting a request will not cause a retry.


## [Release 2.2.7](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.7) (10/08/2015)

### New Features
- **AWS Lambda**: You can now maintain multiple versions of your Lambda function code. Versioning allows you to control which Lambda function version is executed in your different environments (e.g., development, testing, or production).  You can also set up your Lambda functions to run for up to five minutes allowing longer running functions such as large volume data ingestion and processing jobs.

### Bug Fixes
- **Amazon S3**: Resolved a hostname verification issue when there is a . in the bucket name. [#59](https://github.com/aws/aws-sdk-android/issues/59) 
- **Amazon S3**: Resolved an issue when using  SSE-C with Transfer Manager 
- **Amazon API Gateway**: Fixed an issue where the incorrect content length was sent to the service when the body contained UTF-8 characters that were multiple bytes in length. [#62](https://github.com/aws/aws-sdk-android/issues/62)


## [Release 2.2.6](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.6) (09/17/2015)

### New Features
- **Amazon Web Services**: General service updates and documentation improvements.
- **Amazon S3**: Added support of ObjectMetadata for upload in Amazon S3 `TransferUtility`. [#56](https://github.com/aws/aws-sdk-android/issues/56)

### Bug Fixes
- **AWS Core Runtime Library**: Fixed a potential NPE issue caused by ResponseCache by explicitly disabling HTTP response cache.
- **Amazon API Gateway**: Fixed a bug where the region for SigV4 signing is pinned to us-east-1. Now the region is deduced from API's invoke URL and can optionally be overwritten via `ApiClientFactory.region(String)`.


## [Release 2.2.5](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.5) (08/07/2015)

### New Features
- **Amazon Web Services**: General service updates and documentation improvements on Amazon Elastic Compute Cloud (EC2) and Auto Scaling.
- **Github**: Open sourced unit tests and added this changelog.

### Bug Fixes
- **Amazon S3**: Fixed a bug when using Amazon S3 `TransferUtility` on a worker thread. [#51](https://github.com/aws/aws-sdk-android/issues/51)
- **AWS Core Runtime Library**: Fixed a bug caused by improper closing of a GZIP encoded content stream. See [AWS forum](https://forums.aws.amazon.com/thread.jspa?threadID=204659).
- **Maven**: Changed the dependency scope of Apache Commons Logging to [`provided`](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html) which can remove compilation warnings and improve capability with Gradle.


## [Release 2.2.4](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.4) (07/22/2015)

### New Features
- **Amazon S3**: The S3 transfer utility has been added to the SDK, which replaces the now deprecated transfer manager. This utility automatically pauses and resumes transfers when internet connectivity is lost and reestablished. The utility also automatically pauses transfers if an app crashes. Developers can manually pause and resume transfers without having to persist any data themselves. For more information, see our [blog](http://mobile.awsblog.com/post/Tx2KF0YUQITA164/AWS-SDK-for-Android-Transfer-Manager-to-Transfer-Utility-Migration-Guide) and [Getting Started documentation](http://docs.aws.amazon.com/mobile/sdkforandroid/developerguide/s3transferutility.html) for more info.
- **Amazon Mobile Analytics**: A new simpler constructor has been added to the MobileAnalyticsManager, and transmission of events over WAN is now enabled by default.


## [Release 2.2.3](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.3) (07/09/2015)

### New Features
- **Amazon API Gateway**: Added a runtime library for the generated SDK of Amazon API Gateway. Amazon API Gateway makes it easy for AWS customers to publish, maintain, monitor, and secure application programming interfaces (APIs) at any scale. To know more please visit [Amazon API Gateway]( http://aws.amazon.com/api-gateway/).

### Bug Fixes
- **Amazon S3**: Fixed an issue in Amazon S3 where the range information is incorrect in PersistableTransfer. [#35](https://github.com/aws/aws-sdk-android/issues/35)
- **AWS Core Runtime Library**: Fixed an issue in `CognitoCachingCredentialsProvider` where the credentials might not be cached when using developer-authenticated identities. [#48](https://github.com/aws/aws-sdk-android/issues/48)


## [Release 2.2.2](https://github.com/aws/aws-sdk-android/releases/tag/release_v2.2.2) (06/11/2015)

### New Features
- **AWS Core Runtime Library**: Enabled HTTP compression by adding "Accept-Encoding:gzip" header. If the target AWS service (for example, Amazon DynamoDB) supports compression and returns compressed data, the SDK will handle the content correctly. [#41](https://github.com/aws/aws-sdk-android/issues/41)
- **Amazon Kinesis**: KinesisRecorder now sends compressed records to Amazon Kinesis.
- **Amazon Mobile Analytics**: Analytics events are compressed prior to sending the service in order to save network bandwidth.

### Bug Fixes
- **Amazon S3**: Fixed an issue that occurs when required headers are not properly signed. This issue affects S3 in two regions: Frankfurt (eu-central-1) and China (cn-north-1). [#42](https://github.com/aws/aws-sdk-android/issues/42)
- **AWS Core Runtime Library**: Fixed an issue in Maven distribution where an incorrect version string is set in "User-Agent".
