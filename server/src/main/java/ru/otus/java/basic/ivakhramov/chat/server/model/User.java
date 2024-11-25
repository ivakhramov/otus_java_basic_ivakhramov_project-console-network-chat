package ru.otus.java.basic.ivakhramov.chat.server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The User class represents a user in the chat server application.
 * It contains user-related information such as ID, login credentials,
 * name, and roles assigned to the user.
 */
public class User {

    private int id;
    private String login;
    private String password;
    private String name;

    private List<Role> roles = new ArrayList<>();

    /**
     * Constructs a new User with the specified parameters.
     *
     * @param id       the unique identifier for the user
     * @param login    the user's login name
     * @param password the user's password
     * @param name     the user's full name
     * @param roles    the list of roles assigned to the user
     */
    public User(int id, String login, String password, String name, List<Role> roles) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.roles = roles;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Adds a role to the user's list of roles.
     *
     * @param role the role to be added, represented as an EnumRole
     */
    public void addRoleToRoles(EnumRole role) {
        this.roles.add(new Role(role == EnumRole.ADMIN ? 1 : 2, role));
    }

    /**
     * Removes a role from the user's list of roles.
     *
     * @param enumRole the role to be removed, represented as an EnumRole
     */
    public void removeRoleFromRoles(EnumRole enumRole) {
        Iterator<Role> iterator = roles.iterator();
        while (iterator.hasNext()) {
            Role role = iterator.next();
            if (role.getRole().equals(enumRole)) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                '}';
    }
}
