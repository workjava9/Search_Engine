package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"indexEntityByLemmaId", "site"})
@Entity
@Table(
        name = "lemma",
        schema = "search_engine",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_lemma_site", columnNames = {"site_id", "lemma"})
        },
        indexes = {
                @Index(name = "idx_lemma_site", columnList = "site_id"),
                @Index(name = "idx_lemma_word", columnList = "lemma")
        }
)
public class Lemma {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lemma_seq")
    @SequenceGenerator(name = "lemma_seq", sequenceName = "lemma_seq", allocationSize = 50)
    @Column(name = "lemma_id", nullable = false)
    private Integer lemmaId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private SiteEntity site;

    @Column(name = "lemma", nullable = false, length = 255)
    private String lemma;

    @Column(name = "frequency", nullable = false)
    private int frequency = 0;

    @OneToMany(
            mappedBy = "lemmaByLemmaId",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private List<IndexEntity> indexEntityByLemmaId = new ArrayList<>();

    public Lemma(SiteEntity site, String lemma, int frequency) {
        this.site = site;
        this.lemma = lemma;
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lemma that)) return false;
        return lemmaId != null && lemmaId.equals(that.lemmaId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
