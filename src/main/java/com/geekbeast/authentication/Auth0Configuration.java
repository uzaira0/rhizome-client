/*
 * Copyright (C) 2017. OpenLattice, Inc
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can contact the owner of the copyright at support@openlattice.com
 *
 */

package com.geekbeast.authentication;

import com.auth0.json.mgmt.users.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.geekbeast.rhizome.configuration.configuration.annotation.ReloadableConfiguration;
import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT authentication configuration POJO. Despite the Auth0 naming (kept for backward
 * compatibility with existing auth0.yaml files), this class is used for generic
 * self-hosted JWT configuration. The Auth0-cloud-specific fields (domain, clientId,
 * clientSecret, managementApiUrl) are accepted but ignored -- only the JWT
 * {@code configurations} and optional {@code users} list are functionally required.
 *
 * @author Matthew Tamayo-Rios &lt;matthew@kryptnostic.com&gt;
 */
// TODO: Implement data serializable or identified data serializable
@ReloadableConfiguration(
        uri = "auth0.yaml" )
@JsonIgnoreProperties(ignoreUnknown = true)
public class Auth0Configuration implements Serializable {
    private static final long   serialVersionUID = 3802624515206194126L;
    private static final Logger logger           = LoggerFactory.getLogger( Auth0Configuration.class );

    public static final String CONFIGURATIONS_FIELD     = "configurations";
    public static final String USERS_FIELD              = "users";

    // Legacy fields -- accepted from YAML for backward compatibility but no longer required
    public static final String DOMAIN_FIELD             = "domain";
    public static final String CLIENT_ID_FIELD          = "clientId";
    public static final String CLIENT_SECRET_FIELD      = "clientSecret";
    public static final String MANAGEMENT_API_URL_FIELD = "managementApiUrl";

    private final String                                domain;
    private final String                                clientId;
    private final String                                clientSecret;
    private final Set<Auth0AuthenticationConfiguration> configurations;
    private final Set<User>                             users;
    private final String                                managementApiUrl;

    @JsonCreator
    public Auth0Configuration(
            @JsonProperty( DOMAIN_FIELD ) String domain,
            @JsonProperty( CLIENT_ID_FIELD ) String clientId,
            @JsonProperty( CLIENT_SECRET_FIELD ) String clientSecret,
            @JsonProperty( CONFIGURATIONS_FIELD ) Set<Auth0AuthenticationConfiguration> configurations,
            @JsonProperty( USERS_FIELD ) Optional<Set<User>> users,
            @JsonProperty( MANAGEMENT_API_URL_FIELD ) String managementApiUrl ) {
        this.domain = domain != null ? domain : "localhost";
        this.clientId = clientId != null ? clientId : "";
        this.clientSecret = clientSecret != null ? clientSecret : "";
        this.configurations = configurations;
        this.managementApiUrl = managementApiUrl != null ? managementApiUrl : "localhost";
        this.users = users.orElse( ImmutableSet.of() );
    }

    /**
     * @deprecated Legacy Auth0 cloud field. No longer used for self-hosted JWT.
     */
    @Deprecated
    @JsonProperty( DOMAIN_FIELD )
    public String getDomain() {
        return domain;
    }

    /**
     * @deprecated Legacy Auth0 cloud field. No longer used for self-hosted JWT.
     */
    @Deprecated
    @JsonProperty( CLIENT_ID_FIELD )
    public String getClientId() {
        return clientId;
    }

    /**
     * @deprecated Legacy Auth0 cloud field. No longer used for self-hosted JWT.
     */
    @Deprecated
    @JsonProperty( CLIENT_SECRET_FIELD )
    public String getClientSecret() {
        return clientSecret;
    }

    @JsonProperty( CONFIGURATIONS_FIELD )
    public Set<Auth0AuthenticationConfiguration> getClients() {
        return configurations;
    }

    /**
     * @deprecated Legacy Auth0 cloud field. No longer used for self-hosted JWT.
     */
    @Deprecated
    @JsonProperty( MANAGEMENT_API_URL_FIELD )
    public String getManagementApiUrl() {
        return managementApiUrl;
    }

    @JsonProperty( USERS_FIELD )
    public Set<User> getUsers() {
        return users;
    }
}
