package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"pageByPageId", "lemmaByLemmaId"})
@Entity
@Table(
        name = "search_index",
        schema = "search_engine",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_search_index_page_lemma", columnNames = {"page_id", "lemma_id"})
        },
        indexes = {
                @Index(name = "idx_si_page", columnList = "page_id"),
                @Index(name = "idx_si_lemma", columnList = "lemma_id")
        }
)
public class IndexEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idx_seq")
    @SequenceGenerator(name = "idx_seq", sequenceName = "idx_seq", allocationSize = 50)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "page_id", nullable = false)
    private Page pageByPageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lemma_id", nullable = false)
    private Lemma lemmaByLemmaId;

    @Column(name = "rank_index", nullable = false)
    private double rank;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexEntity that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
