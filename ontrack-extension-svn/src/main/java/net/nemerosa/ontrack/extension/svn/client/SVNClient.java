package net.nemerosa.ontrack.extension.svn.client;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.model.SVNHistory;
import net.nemerosa.ontrack.extension.svn.model.SVNReference;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionPath;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.List;
import java.util.Optional;

public interface SVNClient {

    boolean exists(SVNRepository repository, SVNURL url, SVNRevision revision);

    long getRepositoryRevision(SVNRepository repository, SVNURL url);

    void log(SVNRepository repository, SVNURL url, SVNRevision pegRevision, SVNRevision startRevision, SVNRevision stopRevision,
             boolean stopOnCopy, boolean discoverChangedPaths, long limit, boolean includeMergedRevisions,
             ISVNLogEntryHandler isvnLogEntryHandler);

    /**
     * Gets the Subversion information about a URL in a repository
     *
     * @param repository Repository configuration
     * @param url        URL to get the info about
     * @param revision   Revision
     * @return Subversion information
     */
    SVNInfo getInfo(SVNRepository repository, SVNURL url, SVNRevision revision);

    boolean isBranch(SVNRepository repository, String path);

    boolean isTrunk(String path);

    boolean isTrunkOrBranch(SVNRepository repository, String path);

    boolean isTagOrBranch(SVNRepository repository, String path);

    boolean isTag(SVNRepository repository, String path);

    List<Long> getMergedRevisions(SVNRepository repository, SVNURL url, long revision);

    SVNReference getReference(SVNRepository repository, String path);

    SVNHistory getHistory(SVNRepository repository, String path);

    List<SVNRevisionPath> getRevisionPaths(SVNRepository repository, long revision);

    List<String> getBranches(SVNRepository repository, SVNURL url);

    String getDiff(SVNRepository repository, String path, List<Long> revisions);

    Optional<String> download(SVNRepository repository, String path);

    Optional<String> getBasePath(SVNRepository svnRepository, String branchPath);
}
