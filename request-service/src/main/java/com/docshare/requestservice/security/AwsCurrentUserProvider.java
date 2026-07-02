package com.docshare.requestservice.security;

import com.docshare.common.dto.CurrentUser;
import com.docshare.requestservice.security.CurrentUserProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev","prod"})
public class AwsCurrentUserProvider implements CurrentUserProvider {

    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    public static void set(CurrentUser user) {

        HOLDER.set(user);

    }

    @Override
    public CurrentUser getCurrentUser() {

        return HOLDER.get();

    }

    public static void clear() {

        HOLDER.remove();

    }

}