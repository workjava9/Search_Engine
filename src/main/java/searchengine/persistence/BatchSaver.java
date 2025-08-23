package searchengine.persistence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class BatchSaver {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public <T> void persistAll(List<T> entities) {
        for (int i = 0; i < entities.size(); i++) {
            em.persist(entities.get(i));
            int batchSize = 50;
            if ((i + 1) % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }
        em.flush();
        em.clear();
    }
}
