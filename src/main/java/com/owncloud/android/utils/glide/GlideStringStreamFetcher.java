/**
 * Nextcloud Android client application
 *
 * @author Alejandro Bautista
 * Copyright (C) 2017 Alejandro Bautista
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU AFFERO GENERAL PUBLIC LICENSE for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.owncloud.android.utils.glide;

import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.owncloud.android.MainApp;
import com.owncloud.android.authentication.AccountUtils;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.utils.Log_OC;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.InputStream;

/**
 * Fetcher with OwnCloudClient
 */

public class GlideStringStreamFetcher implements DataFetcher<InputStream> {

    private static final String TAG = GlideStringStreamFetcher.class.getName();
    private final String url;

    public GlideStringStreamFetcher(String url) {
        this.url = url;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        OwnCloudClient client = AccountUtils.getClientForCurrentAccount(MainApp.getAppContext());

        GetMethod get = null;
        try {
            get = new GetMethod(url);
            get.setRequestHeader("Cookie", "nc_sameSiteCookielax=true;nc_sameSiteCookiestrict=true");
            get.setRequestHeader(RemoteOperation.OCS_API_HEADER, RemoteOperation.OCS_API_HEADER_VALUE);
            int status = client.executeMethod(get);
            if (status == HttpStatus.SC_OK) {
                callback.onDataReady(get.getResponseBodyAsStream());
            } else {
                client.exhaustResponse(get.getResponseBodyAsStream());
            }
        } catch (Exception e) {
            Log_OC.e(TAG, e.getMessage(), e);
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
    }

    public void cleanup() {
        Log_OC.i(TAG, "Cleanup");
    }

    @Override
    public void cancel() {
        Log_OC.i(TAG, "Cancel");
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
