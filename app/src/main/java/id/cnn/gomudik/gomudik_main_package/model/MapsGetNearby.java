package id.cnn.gomudik.gomudik_main_package.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MapsGetNearby {
    @SerializedName("html_attributions")
    private ArrayList html_attributions;
    @SerializedName("results")
    private List<Results> results;
    @SerializedName("status")
    private String status;
    public ArrayList getHtml_attributions() {
        return html_attributions;
    }
    public List<Results> getResults() {
        return results;
    }
    public String getStatus() {
        return status;
    }
    public class Results {
        @SerializedName("geometry")
        private Geometry geometry;
        @SerializedName("icon")
        private String icon;
        @SerializedName("id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("place_id")
        private String place_id;
        @SerializedName("plus_code")
        private PlusCode plus_code;
        @SerializedName("rating")
        private Float rating;
        @SerializedName("reference")
        private String reference;
        @SerializedName("scope")
        private String scope;
        @SerializedName("types")
        private Object types;
        @SerializedName("vicinity")
        private String vicinity;
        public Results(Geometry geometry, String icon, String id, String name, String place_id, PlusCode plus_code, Float rating, String reference, String scope, Object types, String vicinity){
            this.geometry = geometry;
            this.icon = icon;
            this.id = id;
            this.name = name;
            this.place_id = place_id;
            this.plus_code = plus_code;
            this.rating = rating;
            this.reference = reference;
            this.scope = scope;
            this.types = types;
            this.vicinity = vicinity;
        }
        public Geometry getGeometry() { return geometry; }
        public Float getRating() { return rating; }
        public PlusCode getPlus_code() { return plus_code; }
        public String getIcon() { return icon; }
        public String getId() { return id; }
        public String getName() { return name; }
        public String getPlace_id() { return place_id; }
        public String getReference() { return reference; }
        public String getScope() { return scope; }
        public String getVicinity() { return vicinity; }
        public Object getTypes() { return types; }
    }
    public class Geometry {
        @SerializedName("location")
        private Location location;
        @SerializedName("viewport")
        private Viewport viewport;
        public Geometry(Location location, Viewport viewport){
            this.location = location;
            this.viewport = viewport;
        }
        public Location getLocation() {
            return location;
        }
        public Viewport getViewport() {
            return viewport;
        }
    }
    public class PlusCode {
        @SerializedName("compound_code")
        private String compound_code;
        @SerializedName("global_code")
        private String global_code;
        public PlusCode(String compound_code, String global_code){
            this.compound_code = compound_code;
            this.global_code = global_code;
        }
        public String getCompound_code() {
            return compound_code;
        }
        public String getGlobal_code() {
            return global_code;
        }
    }
    public class Location {
        @SerializedName("lat")
        private Double lat;
        @SerializedName("lng")
        private Double lng;
        public Location(Double lat, Double lng){
            this.lat = lat;
            this.lng = lng;
        }
        public Double getLat() {
            return lat;
        }
        public Double getLng() {
            return lng;
        }
    }
    public class Viewport { }
}