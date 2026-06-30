package com.docshare.requestservice.security;

import com.docshare.common.dto.CurrentUser;

public interface CurrentUserProvider {

    CurrentUser getCurrentUser();

}