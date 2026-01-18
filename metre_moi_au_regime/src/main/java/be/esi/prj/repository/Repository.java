package be.esi.prj.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic interface for data persistence operations.
 * Defines standard CRUD operations for accessing and manipulating entities.
 *
 * @param <K> The type of primary key used to identify entities
 * @param <T> The type of entity managed by this repository
 */
public interface Repository<K, T> {
    /**
     * Finds an entity by its primary key.
     *
     * @param key The primary key of the entity to find
     * @return An Optional containing the entity if found, empty otherwise
     */
    Optional<T> findById(K key);

    /**
     * Retrieves all available entities.
     *
     * @return A list of all entities
     */
    List<T> findAll();

    /**
     * Saves an entity (create or update).
     *
     * @param item The entity to save
     * @return The primary key of the saved entity
     */
    K save(T item);

    /**
     * Deletes an entity by its primary key.
     *
     * @param key The primary key of the entity to delete
     */
    void deleteById(K key);

    /**
     * Releases resources used by this repository.
     * Should be called when the repository is no longer needed.
     */
    void close();
}