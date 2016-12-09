package com.izv.dam.newquip.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;

public class Localizacion implements Parcelable{


    @DatabaseField
    long idnota;

    @DatabaseField
    float lat;

    @DatabaseField
    float lon;

    public Localizacion() {
        this(0,(float)0.0,(float)0.0);
    }

    public Localizacion(long idnota, float lat, float lon) {
        this.idnota = idnota;
        this.lat = lat;
        this.lon = lon;
    }

    protected Localizacion(Parcel in) {
        idnota = in.readLong();
        lat = in.readFloat();
        lon = in.readFloat();
    }

    public static final Creator<Localizacion> CREATOR = new Creator<Localizacion>() {
        @Override
        public Localizacion createFromParcel(Parcel in) {
            return new Localizacion(in);
        }

        @Override
        public Localizacion[] newArray(int size) {
            return new Localizacion[size];
        }
    };

    public long getIdnota(){
        return idnota;
    }

    public void setIdnota(long idnota){
        this.idnota = idnota;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "Localizacion{" +
                "id=" + idnota +
                ", latitude=" + lat +
                ", longitude=" + lon +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(idnota);
        dest.writeFloat(lat);
        dest.writeFloat(lon);
    }
}
