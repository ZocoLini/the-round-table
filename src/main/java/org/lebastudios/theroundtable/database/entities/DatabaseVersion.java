package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "core_database_version")
public class DatabaseVersion
{
    @Id
    @Column(name = "plugin_identifier")
    private String pluginIdentifier;

    @Column(name = "version")
    private int version;
}
