package be.esi.prj.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object interface that defines basic CRUD operations.
 * Provides a standard way to interact with data storage for various entity types.
 *
 * @param <K> The type of keys/IDs used to identify entities
 * @param <T> The type of entities managed by this DAO
 */
public interface Dao<K, T> {
    /**
     * Finds an entity by its ID.
     *
     * @param id The ID of the entity to find
     * @return An Optional containing the entity if found, empty otherwise
     */
    Optional<T> findById(Integer id);

    /**
     * Retrieves all entities from the data source.
     *
     * @return A list of all entities
     */
    List<T> findAll();

    /**
     * Saves an entity to the data source (insert or update).
     *
     * @param item The entity to save
     * @return The key/ID of the saved entity
     */
    K save(T item);

    /**
     * Deletes an entity by its ID.
     *
     * @param id The ID of the entity to delete
     */
    void deleteById(Integer id);
}
