package searchengine.model;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "page",
        schema = "search_engine",
        indexes = {
                @Index(name = "idx_page_site_path", columnList = "site_id,path", unique = true)
        }
)
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "page_seq")
    @SequenceGenerator(name = "page_seq", sequenceName = "page_seq")
    @Column(name = "page_id", nullable = false)
    private Integer pageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private SiteEntity siteEBySiteId;

    @Column(name = "path", nullable = false, length = 255)
    private String path;

    @Column(name = "code", nullable = false)
    private int code;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @OneToMany(
            mappedBy = "pageByPageId",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private List<IndexEntity> indexEntityByPageId = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Page that)) return false;
        return pageId != null && pageId.equals(that.pageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageId, siteEBySiteId, path, code, content);
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageId=" + pageId +
                ", siteId=" + siteEBySiteId +
                ", path='" + path + '\'' +
                ", code=" + code +
                ", content(trim by 100)='" + content.substring(0, 30) + '\'' +
                '}';
    }
}
