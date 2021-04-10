package net.senior.model;

import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class Source {
    private DocumentReference reference;
    private String name;
    private double lng;
    private double lat;

    public DocumentReference getReference() {
        return reference;
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }
    public Source(DocumentReference reference) {
        this.reference = reference;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Source(DocumentReference reference, String name, double lng,double lat) {
        this(reference) ;
        this.name = name;
        this.lng = lng;
        this.lat = lat;

    }

    public Source(DocumentReference reference, Map<String, Object> data) {
        this (reference);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            switch (key) {
                case Contract.FIELD_NAME:
                    name = (String) value;
                    break;
                case Contract.FIELD_lat:
                    lat = (Double) value;
                    break;
                    case Contract.FIELD_lng:
                    lng = (Double) value;
                    break;
                       }
        }
    }
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(Contract.FIELD_lat, lat);
        map.put(Contract.FIELD_lng, lng);
        map.put(Contract.FIELD_NAME, name);

        return map;
    }
public static class Contract {
    public static final String DOC = "source";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_lng = "lng";
    public static final String FIELD_lat = "lat";

} }


