package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Session;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.events.DatabaseEntitiesEvents;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "CATEGORY")
public class Category
{
    static
    {
        DatabaseEntitiesEvents.onProductsModified.addListener(Category::cleanupCategories);
    }

    @Id
    @Column(name = "NAME")
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category", cascade = CascadeType.REMOVE)
    private Set<SubCategory> subCategories;

    public static void cleanupCategories()
    {
        Database.getInstance().connectTransaction(session ->
        {
            var categories = session.createQuery("from Category", Category.class)
                    .getResultList();

            categories.forEach(category -> cleanupCategory(session, category));
        });
    }

    private static void cleanupCategory(Session session, Category category)
    {
        boolean conservesAnySubcategory = false;
        var list = new ArrayList<>(category.getSubCategories());
        for (var subcategories : list)
        {
            if (subcategories.getProducts().isEmpty())
            {
                session.remove(subcategories);
            }
            else
            {
                conservesAnySubcategory = true;
            }
        }

        if (!conservesAnySubcategory) session.remove(category);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Category category)) return false;

        return Objects.equals(name, category.name);
    }
}
