package service;

import java.util.List;

public interface CrudService<T, ID> {
    void create(T entity);
    T read(ID id);
    List<T> readAll();
    void update(ID id, T entity);
    void delete(ID id);
}
