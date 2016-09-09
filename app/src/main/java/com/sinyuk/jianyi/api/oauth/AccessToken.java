package com.sinyuk.jianyi.api.oauth;

import com.google.gson.annotations.SerializedName;

public final class AccessToken {

    /**
     * access_token : 29ed478ab86c07f1c069b1af76088f7431396b7c4a2523d06911345da82224a0
     * token_type : bearer
     * scope : public write
     */

    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("scope")
    private String scope;

    public String getTokenType() {
        // OAuth requires uppercase Authorization HTTP header value for token type
        if (!Character.isUpperCase(tokenType.charAt(0))) {
            tokenType =
                    Character
                            .toString(tokenType.charAt(0))
                            .toUpperCase() + tokenType.substring(1);
        }

        return tokenType;
    }

    public void setTokenType(String tokenType) { this.tokenType = tokenType;}

    public String getAccessToken() { return accessToken;}

    public void setAccessToken(String accessToken) { this.accessToken = accessToken;}

    public String getScope() { return scope;}

    public void setScope(String scope) { this.scope = scope;}

    @Override
    public String toString() {
        return "AccessToken{" +
                "accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}