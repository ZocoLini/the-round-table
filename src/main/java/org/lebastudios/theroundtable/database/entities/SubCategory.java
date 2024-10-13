package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "SUB_CATEGORY")
public class SubCategory
{
    @EmbeddedId
    private SubCategoryId id;

    @MapsId("category_Name")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "CATEGORY_NAME", referencedColumnName = "NAME")
    private Category category;

    @OneToMany(mappedBy = "subCategory", fetch = FetchType.LAZY)
    private Set<Product> products;

    @Override
    public int hashCode()
    {
        return Objects.hashCode(id);
    }

    @Override
    public final boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SubCategory that)) return false;

        return Objects.equals(id, that.id);
    }

    @Embeddable
    public record SubCategoryId(String category_Name, String name) {}
}
