/*
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.services.iotdata.model;

import java.io.Serializable;

import com.amazonaws.AmazonWebServiceRequest;

/**
 * Container for the parameters to the {@link com.amazonaws.services.iotdata.AWSIotData#updateThingShadow(UpdateThingShadowRequest) UpdateThingShadow operation}.
 * <p>
 * Updates the thing shadow for the specified thing.
 * </p>
 * <p>
 * For more information, see
 * <a href="http://docs.aws.amazon.com/iot/latest/developerguide/API_UpdateThingShadow.html"> UpdateThingShadow </a>
 * in the <i>AWS IoT Developer Guide</i> .
 * </p>
 *
 * @see com.amazonaws.services.iotdata.AWSIotData#updateThingShadow(UpdateThingShadowRequest)
 */
public class UpdateThingShadowRequest extends AmazonWebServiceRequest implements Serializable {

    /**
     * The name of the thing.
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 128<br/>
     * <b>Pattern: </b>[a-zA-Z0-9_-]+<br/>
     */
    private String thingName;

    /**
     * The state information, in JSON format.
     */
    private java.nio.ByteBuffer payload;

    /**
     * The name of the thing.
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 128<br/>
     * <b>Pattern: </b>[a-zA-Z0-9_-]+<br/>
     *
     * @return The name of the thing.
     */
    public String getThingName() {
        return thingName;
    }
    
    /**
     * The name of the thing.
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 128<br/>
     * <b>Pattern: </b>[a-zA-Z0-9_-]+<br/>
     *
     * @param thingName The name of the thing.
     */
    public void setThingName(String thingName) {
        this.thingName = thingName;
    }
    
    /**
     * The name of the thing.
     * <p>
     * Returns a reference to this object so that method calls can be chained together.
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 128<br/>
     * <b>Pattern: </b>[a-zA-Z0-9_-]+<br/>
     *
     * @param thingName The name of the thing.
     *
     * @return A reference to this updated object so that method calls can be chained
     *         together.
     */
    public UpdateThingShadowRequest withThingName(String thingName) {
        this.thingName = thingName;
        return this;
    }

    /**
     * The state information, in JSON format.
     *
     * @return The state information, in JSON format.
     */
    public java.nio.ByteBuffer getPayload() {
        return payload;
    }
    
    /**
     * The state information, in JSON format.
     *
     * @param payload The state information, in JSON format.
     */
    public void setPayload(java.nio.ByteBuffer payload) {
        this.payload = payload;
    }
    
    /**
     * The state information, in JSON format.
     * <p>
     * Returns a reference to this object so that method calls can be chained together.
     *
     * @param payload The state information, in JSON format.
     *
     * @return A reference to this updated object so that method calls can be chained
     *         together.
     */
    public UpdateThingShadowRequest withPayload(java.nio.ByteBuffer payload) {
        this.payload = payload;
        return this;
    }

    /**
     * Returns a string representation of this object; useful for testing and
     * debugging.
     *
     * @return A string representation of this object.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (getThingName() != null) sb.append("ThingName: " + getThingName() + ",");
        if (getPayload() != null) sb.append("Payload: " + getPayload() );
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;
        
        hashCode = prime * hashCode + ((getThingName() == null) ? 0 : getThingName().hashCode()); 
        hashCode = prime * hashCode + ((getPayload() == null) ? 0 : getPayload().hashCode()); 
        return hashCode;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        if (obj instanceof UpdateThingShadowRequest == false) return false;
        UpdateThingShadowRequest other = (UpdateThingShadowRequest)obj;
        
        if (other.getThingName() == null ^ this.getThingName() == null) return false;
        if (other.getThingName() != null && other.getThingName().equals(this.getThingName()) == false) return false; 
        if (other.getPayload() == null ^ this.getPayload() == null) return false;
        if (other.getPayload() != null && other.getPayload().equals(this.getPayload()) == false) return false; 
        return true;
    }
    
}
    