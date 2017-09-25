package nl.open.mail;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;

public class DefaultTemplateRefCache {
    private static NodeRef defaultTemplateRef;

    // TODO better caching
    public static NodeRef getValue(final String path, final SearchService searchService) {
        if (defaultTemplateRef == null) {
            synchronized (DefaultTemplateRefCache.class) {
                if (defaultTemplateRef == null) {
                    final SearchParameters sp = new SearchParameters();
                    sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                    sp.setQuery(String.format("PATH:\"\"%s\"", path));
                    sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
                    sp.setMaxItems(1);

                    final NodeRef nr = AuthenticationUtil.runAsSystem(new RunAsWork<NodeRef>() {
                        @Override
                        public NodeRef doWork() throws Exception {
                            final ResultSet rs = searchService.query(sp);
                            if (rs.length() == 1) {
                                return rs.getNodeRef(0);
                            } else {
                                throw new IllegalArgumentException("Unresolvable path: " + path);
                            }
                        }
                    });
                    
                    defaultTemplateRef = nr;
                }
            }
        }

        return defaultTemplateRef;
    }
}
