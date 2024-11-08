package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lebastudios.theroundtable.locale.LangFileLoader;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "core_account")
public class Account
{
    public Account(String name, String password, AccountType type)
    {
        this.name = name;
        this.password = password;
        this.type = type;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type = AccountType.CASHIER;

    @Column(name = "changue_password_on_next_login", nullable = false)
    private boolean changePasswordOnNextLogin = false;

    public boolean hasAuthorityOver(Account account)
    {
        if (Objects.equals(this.id, account.id)) return true;

        return switch (type)
        {
            case ROOT -> true;
            case ADMIN -> account.getType() != AccountType.ROOT && account.getType() != AccountType.ADMIN;
            case MANAGER -> account.getType() != AccountType.ROOT
                    && account.getType() != AccountType.ADMIN && account.getType() != AccountType.MANAGER;
            case CASHIER, ACCOUNTANT -> false;
        };
    }

    public String getTypeString()
    {
        return getTypeString(type);
    }

    public static String getTypeString(AccountType type)
    {
        return LangFileLoader.getTranslation("enum.accounttype." + type.name().toLowerCase());
    }

    public String getIconName()
    {
        return getIconName(type);
    }

    public static String getIconName(AccountType type)
    {
        return switch (type)
        {
            case ROOT, ADMIN -> "admin-user.png";
            default -> "user.png";
        };
    }

    public enum AccountType
    {
        ROOT,
        ADMIN,
        MANAGER,
        CASHIER,
        ACCOUNTANT
    }
}
