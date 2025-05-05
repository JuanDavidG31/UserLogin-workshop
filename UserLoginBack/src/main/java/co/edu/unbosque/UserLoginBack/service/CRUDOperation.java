package co.edu.unbosque.UserLoginBack.service;

import java.util.List;

public interface CRUDOperation<T, E> {

	public int create(T data);

	public List<T> getAll();

	public int deleteById(Long id);

	public int updateById(Long id, T newData);

	public long count();

	public boolean exist(Long id);

	public E encrypt(T data);

	public String decrypt(T data);
}
