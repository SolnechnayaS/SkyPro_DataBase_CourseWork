import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EMF {

    protected static EntityManager emfCreate () {
        // Создаем экземпляр EntityManagerFactory, указывая persistence unit
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit");

        // Создаем экземпляр EntityManager из EntityManagerFactory
        return entityManagerFactory.createEntityManager();

    }

}
