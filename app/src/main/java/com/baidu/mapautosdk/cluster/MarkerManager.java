/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

package com.baidu.mapautosdk.cluster;

import android.view.View;

import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.interfaces.map.OnMarkerClickListener;
import com.baidu.platform.comapi.sdk.map.Marker;
import com.baidu.platform.comapi.sdk.map.MarkerOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Keeps track of collections of markers on the map. Delegates all Marker-related events to each
 * collection's individually managed listeners.
 * <p/>
 * All marker operations (adds and removes) should occur via its collection class. That is, don't
 * add a marker via a collection, then remove it via Marker.remove()
 */
public class MarkerManager implements OnMarkerClickListener {
    private final View mMap;

    private final Map<String, Collection> mNamedCollections = new HashMap<String, Collection>();
    private final Map<Marker, Collection> mAllMarkers = new HashMap<Marker, Collection>();

    public MarkerManager(View map) {
        this.mMap = map;
    }

    public Collection newCollection() {
        return new Collection();
    }

    /**
     * Create a new named collection, which can later be looked up by {@link #getCollection(String)}
     * @param id a unique id for this collection.
     */
    public Collection newCollection(String id) {
        if (mNamedCollections.get(id) != null) {
            throw new IllegalArgumentException("collection id is not unique: " + id);
        }
        Collection collection = new Collection();
        mNamedCollections.put(id, collection);
        return collection;
    }

    /**
     * Gets a named collection that was created by {@link #newCollection(String)}
     * @param id the unique id for this collection.
     */
    public Collection getCollection(String id) {
        return mNamedCollections.get(id);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        if (collection != null && collection.mMarkerClickListener != null) {
            // you can set the click action
            return collection.mMarkerClickListener.onMarkerClick(marker);
        } else  {
            ; // click single maker out of cluster
        }
        return false;
    }

    /**
     * Removes a marker from its collection.
     *
     * @param marker the marker to remove.
     * @return true if the marker was removed.
     */
    public boolean remove(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        return collection != null && collection.remove(marker);
    }

    public class Collection {
        private final Set<Marker> mMarkers = new HashSet<Marker>();
        private OnMarkerClickListener mMarkerClickListener;

        public Collection() {
        }

        public Marker addMarker(MarkerOptions opts) {
            Marker marker = (Marker) BDAutoMapFactory.getAutoMapManager().addSDKOverlayItem(opts);
            mMarkers.add(marker);
            mAllMarkers.put(marker, Collection.this);
            return marker;
        }

        public boolean remove(Marker marker) {
            if (mMarkers.remove(marker)) {
                mAllMarkers.remove(marker);
                marker.remove();
                return true;
            }
            return false;
        }

        public void clear() {
            for (Marker marker : mMarkers) {
                marker.remove();
                mAllMarkers.remove(marker);
            }
            mMarkers.clear();
        }

        public java.util.Collection<Marker> getMarkers() {
            return Collections.unmodifiableCollection(mMarkers);
        }

        public void setOnMarkerClickListener(OnMarkerClickListener markerClickListener) {
            mMarkerClickListener = markerClickListener;
        }
    }
}
