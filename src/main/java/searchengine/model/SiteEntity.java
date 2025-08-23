package searchengine.model;

import lombok.*;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "site", schema = "search_engine")
public class SiteEntity {

    @Id
    @Column(name = "site_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int siteId;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')")
    private Status status;

    @NonNull
    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @NonNull
    @Column(columnDefinition = "VARCHAR(255)")
    private String url;

    @NonNull
    @Column(columnDefinition = "VARCHAR(255)")
    private String name;

    @OneToMany(mappedBy = "siteEBySiteId", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Lemma> lemmaBySiteId = new ArrayList<>();

    @OneToMany(mappedBy = "siteEBySiteId", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Page> pageBySiteId = new ArrayList<>();

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
