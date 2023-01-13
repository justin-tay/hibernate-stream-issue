package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.util.List;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM,
 * using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");

		// Setup data
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		User user1 = new User();
		user1.setUsername("user1");

		entityManager.persist(user1);

		Appointment appointment1 = new Appointment();
		appointment1.setOwner(user1);
		appointment1.setCreatedBy(user1);
		appointment1.setName("Visit Gym");
		appointment1.setDescription("Exercise");

		entityManager.persist(appointment1);

		entityManager.getTransaction().commit();

		entityManager.close();

	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	@Test
	public void givenGetResultListCircularFetchImplShouldFetch() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();

			Query query = entityManager.createQuery("SELECT a FROM Appointment a WHERE a.owner.username = ?1");
			query.setParameter(1, "user1");
			List<Appointment> result = query.getResultList();
			Assert.assertNotNull(result.get(0).getOwner()); // Works as BatchEntitySelectFetchInitializer endLoading is
															// called

			entityManager.getTransaction().commit();
		} catch (Error e) {
			entityManager.getTransaction().rollback();
			throw e;
		} finally {
			entityManager.close();
		}
	}

	@Test
	public void givenStreamIteratorCircularFetchImplShouldFetch() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();

			Query query = entityManager.createQuery("SELECT a FROM Appointment a WHERE a.owner.username = ?1");
			query.setParameter(1, "user1");
			try (Stream<Appointment> stream = query.getResultStream()) {
				Appointment appointment = stream.iterator().next();
				Assert.assertNotNull(appointment.getOwner()); // Doesn't work as BatchEntitySelectFetchInitializer
																// endLoading is only called when the stream is closed
																// Note that Person is annotated with @BatchSize > 1
			}
			entityManager.getTransaction().commit();
		} catch (Error e) {
			entityManager.getTransaction().rollback();
			throw e;
		} finally {
			entityManager.close();
		}
	}

	@Test
	public void givenStreamFindFirstCircularFetchImplShouldFetch() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();

			Query query = entityManager.createQuery("SELECT a FROM Appointment a WHERE a.owner.username = ?1");
			query.setParameter(1, "user1");
			try (Stream<Appointment> stream = query.getResultStream()) {
				Appointment appointment = stream.findFirst().get();
				Assert.assertNotNull(appointment.getOwner()); // Doesn't work as BatchEntitySelectFetchInitializer
																// endLoading is only called when the stream is closed
																// Note that Person is annotated with @BatchSize > 1
			}

			entityManager.getTransaction().commit();
		} catch (Error e) {
			entityManager.getTransaction().rollback();
			throw e;
		} finally {
			entityManager.close();
		}
	}
}
