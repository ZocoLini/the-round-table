package org.lebastudios.theroundtable.database.entities;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.lebastudios.theroundtable.language.LangFileLoader;

import java.util.Set;

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
        this.password = BCrypt.withDefaults().hashToString(12, password.toCharArray());
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
    
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) @ToString.Exclude
    private Set<Receipt> receipts;

    public String getTypeString() {
        return getTypeString(type);
    }

    public static String getTypeString(AccountType type) {
        return LangFileLoader.getTranslation("enum.accounttype." + type.name().toLowerCase());
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
