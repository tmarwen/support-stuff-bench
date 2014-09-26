package org.exoplatform.support.clog.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/**
 * Show the use of a jQuery Gridster plugin with the built-in jQuery
 *
 * @author <a href="marwen.trabelsi.insat@gmail.com">Marwen Trabelsi</a>
 * @version $Revision$
 */
public class GridsterPortlet extends GenericPortlet {

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/gridster/gridster.jsp");
        prd.include(request, response);
    }
}
