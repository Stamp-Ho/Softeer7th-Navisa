package com.navisa.be.chat.model.entity;

import java.security.Principal;

public record ChatUserPrincipal(String name) implements Principal {
    @Override
    public String getName() { return name; }
}
