package org.exoplatform.support.injector.worker;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by eXo Platform MEA on 26/08/14.
 *
 * @author <a href="mailto:mtrabelsi@exoplatform.com">Marwen Trabelsi</a>
 */
public class SpaceCreator implements Runnable
{
  private final Logger LOG = LoggerFactory.getLogger("org.exoplatform.support.injector.worker.SpaceCreator");
  private final String SPACE_INJECTION_URL;
  private final CloseableHttpClient httpClient;
  private final String spaceNumber;
  private final String fromUser;
  private final String toUser;
  private final String userPrefix;
  private final String spacePrefix;

  public SpaceCreator(CloseableHttpClient httpClient,
                      int spaceNumber,
                      String spaceInjectionURL,
                      int fromUser,
                      int toUser,
                      String userPrefix,
                      String spacePrefix)
  {
    this.httpClient = httpClient;
    this.SPACE_INJECTION_URL = spaceInjectionURL;
    this.spaceNumber = String.valueOf(spaceNumber);
    this.fromUser = String.valueOf(fromUser);
    this.toUser = String.valueOf(toUser);
    this.userPrefix = userPrefix;
    this.spacePrefix = spacePrefix;
  }

  @Override
  public void run()
  {
    CloseableHttpResponse response;
    try
    {
      StringBuilder urlBuilder = new StringBuilder(SPACE_INJECTION_URL);
      String ampersand = "&";
      urlBuilder.append("?number=").append(spaceNumber)
          .append(ampersand).append("fromUser=").append(fromUser)
          .append(ampersand).append("toUser=").append(toUser)
          .append(ampersand).append("userPrefix=").append(userPrefix)
          .append(ampersand).append("spacePrefix=").append(spacePrefix);
      HttpGet injectRequest = new HttpGet(urlBuilder.toString());
      response = httpClient.execute(injectRequest);
      try {
        HttpEntity entity = response.getEntity();

        LOG.info("----------------------------------------");
        if (response.getStatusLine().getStatusCode() == 200)
        {
          LOG.info(String.valueOf(response.getStatusLine()));
          if (entity != null) {
            LOG.info("Response content length: " + entity.getContentLength());
          }
          LOG.info(Thread.currentThread().getName() + " has injected a space successfully ");
        }
        LOG.info("----------------------------------------");
      } finally {
        response.close();
      }
    } catch (UnsupportedEncodingException e)
    {
      LOG.error("An error occurred while injecting the space:", e);
    } catch (ClientProtocolException e)
    {
      LOG.error("An error occurred while injecting the space:", e);
    } catch (IOException e)
    {
      LOG.error("An error occurred while injecting the space:", e);
    }
  }
}
