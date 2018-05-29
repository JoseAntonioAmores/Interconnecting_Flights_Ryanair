
package com.ryanair.connections.connections;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "airportFrom",
    "airportTo",
    "connectingAirport",
    "newRoute",
    "seasonalRoute",
    "operator",
    "group"
})
public class Ruta {

    @JsonProperty("airportFrom")
    private String airportFrom;
    @JsonProperty("airportTo")
    private String airportTo;
    @JsonProperty("connectingAirport")
    private String connectingAirport;
    @JsonProperty("newRoute")
    private Boolean newRoute;
    @JsonProperty("seasonalRoute")
    private Boolean seasonalRoute;
    @JsonProperty("operator")
    private String operator;
    @JsonProperty("group")
    private String group;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("airportFrom")
    public String getAirportFrom() {
        return airportFrom;
    }

    @JsonProperty("airportFrom")
    public void setAirportFrom(String airportFrom) {
        this.airportFrom = airportFrom;
    }

    @JsonProperty("airportTo")
    public String getAirportTo() {
        return airportTo;
    }

    @JsonProperty("airportTo")
    public void setAirportTo(String airportTo) {
        this.airportTo = airportTo;
    }

    @JsonProperty("connectingAirport")
    public String getConnectingAirport() {
        return connectingAirport;
    }

    @JsonProperty("connectingAirport")
    public void setConnectingAirport(String connectingAirport) {
        this.connectingAirport = connectingAirport;
    }

    @JsonProperty("newRoute")
    public Boolean getNewRoute() {
        return newRoute;
    }

    @JsonProperty("newRoute")
    public void setNewRoute(Boolean newRoute) {
        this.newRoute = newRoute;
    }

    @JsonProperty("seasonalRoute")
    public Boolean getSeasonalRoute() {
        return seasonalRoute;
    }

    @JsonProperty("seasonalRoute")
    public void setSeasonalRoute(Boolean seasonalRoute) {
        this.seasonalRoute = seasonalRoute;
    }

    @JsonProperty("operator")
    public String getOperator() {
        return operator;
    }

    @JsonProperty("operator")
    public void setOperator(String operator) {
        this.operator = operator;
    }

    @JsonProperty("group")
    public String getGroup() {
        return group;
    }

    @JsonProperty("group")
    public void setGroup(String group) {
        this.group = group;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Ruta.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("airportFrom");
        sb.append('=');
        sb.append(((this.airportFrom == null)?"<null>":this.airportFrom));
        sb.append(',');
        sb.append("airportTo");
        sb.append('=');
        sb.append(((this.airportTo == null)?"<null>":this.airportTo));
        sb.append(',');
        sb.append("connectingAirport");
        sb.append('=');
        sb.append(((this.connectingAirport == null)?"<null>":this.connectingAirport));
        sb.append(',');
        sb.append("newRoute");
        sb.append('=');
        sb.append(((this.newRoute == null)?"<null>":this.newRoute));
        sb.append(',');
        sb.append("seasonalRoute");
        sb.append('=');
        sb.append(((this.seasonalRoute == null)?"<null>":this.seasonalRoute));
        sb.append(',');
        sb.append("operator");
        sb.append('=');
        sb.append(((this.operator == null)?"<null>":this.operator));
        sb.append(',');
        sb.append("group");
        sb.append('=');
        sb.append(((this.group == null)?"<null>":this.group));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.airportFrom == null)? 0 :this.airportFrom.hashCode()));
        result = ((result* 31)+((this.connectingAirport == null)? 0 :this.connectingAirport.hashCode()));
        result = ((result* 31)+((this.airportTo == null)? 0 :this.airportTo.hashCode()));
        result = ((result* 31)+((this.newRoute == null)? 0 :this.newRoute.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.operator == null)? 0 :this.operator.hashCode()));
        result = ((result* 31)+((this.seasonalRoute == null)? 0 :this.seasonalRoute.hashCode()));
        result = ((result* 31)+((this.group == null)? 0 :this.group.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Ruta) == false) {
            return false;
        }
        Ruta rhs = ((Ruta) other);
        return (((((((((this.airportFrom == rhs.airportFrom)||((this.airportFrom!= null)&&this.airportFrom.equals(rhs.airportFrom)))&&((this.connectingAirport == rhs.connectingAirport)||((this.connectingAirport!= null)&&this.connectingAirport.equals(rhs.connectingAirport))))&&((this.airportTo == rhs.airportTo)||((this.airportTo!= null)&&this.airportTo.equals(rhs.airportTo))))&&((this.newRoute == rhs.newRoute)||((this.newRoute!= null)&&this.newRoute.equals(rhs.newRoute))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.operator == rhs.operator)||((this.operator!= null)&&this.operator.equals(rhs.operator))))&&((this.seasonalRoute == rhs.seasonalRoute)||((this.seasonalRoute!= null)&&this.seasonalRoute.equals(rhs.seasonalRoute))))&&((this.group == rhs.group)||((this.group!= null)&&this.group.equals(rhs.group))));
    }

}
