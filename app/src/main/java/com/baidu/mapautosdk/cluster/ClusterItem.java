/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

package com.baidu.mapautosdk.cluster;

import com.baidu.platform.comapi.sdk.map.BitmapDescriptor;
import com.baidu.platform.comapi.sdk.mapapi.model.LatLng;

/**
 * ClusterItem represents a marker on the map.
 */
public interface ClusterItem {

    /**
     * The position of this marker. This must always return the same value.
     */
    LatLng getPosition();

    BitmapDescriptor getBitmapDescriptor();
}