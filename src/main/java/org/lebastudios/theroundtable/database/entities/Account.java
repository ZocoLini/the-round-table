package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.lebastudios.theroundtable.language.LangFileLoader;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "ACCOUNT")
public class Account
{
    public Account(String name, String password, AccountType type)
    {
        this.name = name;
        this.password = password;
        this.type = type;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type = AccountType.CASHIER;

    @Column(name = "CHANGE_PASSWORD_ON_NEXT_LOGIN", nullable = false)
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
