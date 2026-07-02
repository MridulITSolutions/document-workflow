package com.docshare.requestservice.security;

import com.docshare.common.dto.CurrentUser;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
/**Only invoked for local set up and authentication **/
@Profile("local")
public class LocalCurrentUserProvider implements CurrentUserProvider {

    private static final ThreadLocal<CurrentUser> CURRENT_USER = new ThreadLocal<>();

    public static void set(CurrentUser user){

        CURRENT_USER.set(user);
    }

    @Override
    public CurrentUser getCurrentUser() {
        return CURRENT_USER.get();
    }

}