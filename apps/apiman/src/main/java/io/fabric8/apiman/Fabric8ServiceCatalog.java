/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.apiman;

import io.apiman.manager.api.beans.summary.AvailableServiceBean;
import io.apiman.manager.api.core.IServiceCatalog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple fabric8 version of the apiman service catalog interface.  This
 * implementation will search for services in kubernetes that match the
 * search keyword.
 *
 * @author eric.wittmann@redhat.com
 */
public class Fabric8ServiceCatalog implements IServiceCatalog {

    /**
     * Constructor.
     */
    public Fabric8ServiceCatalog() {
    }

    /**
     * @see io.apiman.manager.api.core.IServiceCatalog#search(java.lang.String)
     */
    @Override
    public List<AvailableServiceBean> search(String keyword) {
        // TODO must implement this!
        return new ArrayList<>();
    }

}
