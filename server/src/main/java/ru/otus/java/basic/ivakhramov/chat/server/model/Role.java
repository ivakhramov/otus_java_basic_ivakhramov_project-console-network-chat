package ru.otus.java.basic.ivakhramov.chat.server.model;

/**
 * The Role class represents a user role in the chat server.
 * It contains an identifier and the specific role assigned to a user.
 */
public class Role {

    private int id;
    private EnumRole role;

    /**
     * Constructs a new Role with the specified id and role.
     *
     * @param id   the unique identifier for the role
     * @param role the EnumRole representing the user's role
     */
    public Role(int id, EnumRole role) {
        this.id = id;
        this.role = role;
    }

    /**
     * Returns the role assigned to the user.
     *
     * @return the EnumRole representing the user's role
     */
    public EnumRole getRole() {
        return role;
    }

    /**
     * Returns a string representation of the Role object.
     *
     * @return a string in the format "Role{id=<id>, role='<role>'}"
     */
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                '}';
    }
}
