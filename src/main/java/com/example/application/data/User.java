package com.example.application.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "application_user")
public class User extends AbstractEntity {

    @NotNull
    private String username;

    private String name;

    @Email
    private String email;

    @NotNull
    @JsonIgnore
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @ManyToMany(mappedBy = "gms")
    private List<Campaign> gmCampaigns;

    @ManyToMany(mappedBy = "players")
    private List<Campaign> playerCampaigns;

    //TODO: Add PCs, and which Campaigns they're in

    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    // Getters & Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Campaign> getGmCampaigns() {
        return gmCampaigns;
    }

    public void setGmCampaigns(List<Campaign> gmCampaigns) {
        this.gmCampaigns = gmCampaigns;
    }

    public List<Campaign> getPlayerCampaigns() {
        return playerCampaigns;
    }

    public void setPlayerCampaigns(List<Campaign> playerCampaigns) {
        this.playerCampaigns = playerCampaigns;
    }
}
