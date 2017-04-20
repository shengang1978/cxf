/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.rs.security.oidc.idp;

import java.util.LinkedList;
import java.util.List;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.services.ClientRegistration;
import org.apache.cxf.rs.security.oauth2.services.ClientRegistrationResponse;
import org.apache.cxf.rs.security.oauth2.services.DynamicRegistrationService;

public class OidcDynamicRegistrationService extends DynamicRegistrationService {
    private static final String RP_INITIATED_LOGOUT_URIS = "post_logout_redirect_uris";
    private boolean protectIdTokenWithClientSecret;

    @Override
    protected Client createNewClient(ClientRegistration request) {
        Client client = super.createNewClient(request);
        List<String> logoutUris = request.getListStringProperty(RP_INITIATED_LOGOUT_URIS);
        if (logoutUris != null) {
            StringBuilder sb = new StringBuilder();
            for (String uri : logoutUris) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(uri);
            }
            client.getProperties().put(RP_INITIATED_LOGOUT_URIS, sb.toString());
        }
        return client;
    }

    @Override
    protected ClientRegistrationResponse fromClientToRegistrationResponse(Client client) {
        return super.fromClientToRegistrationResponse(client);
    }

    @Override
    protected ClientRegistration fromClientToClientRegistration(Client client) {
        ClientRegistration resp = super.fromClientToClientRegistration(client);
        String logoutUris = client.getProperties().get(RP_INITIATED_LOGOUT_URIS);
        if (logoutUris != null) {
            List<String> list = new LinkedList<String>();
            for (String s : logoutUris.split(" ")) { 
                list.add(s);
            }
            resp.setProperty(RP_INITIATED_LOGOUT_URIS, list);
        }
        return resp;
    }

    protected int getClientSecretSizeInBytes(ClientRegistration request) {

        // TODO: may need to be 384/8 or 512/8 if not a default HS256 but HS384 or HS512
        int keySizeOctets = protectIdTokenWithClientSecret
            ? 32
            : super.getClientSecretSizeInBytes(request);

        return keySizeOctets;
    }
    public void setProtectIdTokenWithClientSecret(boolean protectIdTokenWithClientSecret) {
        this.protectIdTokenWithClientSecret = protectIdTokenWithClientSecret;
    }
}
