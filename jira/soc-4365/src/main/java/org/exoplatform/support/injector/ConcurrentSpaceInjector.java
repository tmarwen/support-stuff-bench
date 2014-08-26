package org.exoplatform.support.injector;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.exoplatform.support.injector.worker.SpaceCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by eXo Platform MEA on 26/08/14.
 *
 * @author <a href="mailto:mtrabelsi@exoplatform.com">Marwen Trabelsi</a>
 */
public class ConcurrentSpaceInjector
{
  private static final Logger LOG = LoggerFactory.getLogger("org.exoplatform.support.injector.ConcurrentSpaceInjector");
  private CloseableHttpClient httpClient = HttpClients.createDefault();
  private final int THREAD_POOL_SIZE = 10;

  public static void main(String[] args)
  {
    String userPrefix = "bench.user";
    String spacePrefix = (args.length > 0 && !StringUtils.isEmpty(args[0])) ? args[0] : "aspace";
    String spaceInjectionBaseURL = "http://localhost:8080/rest/private/bench/inject/custom-space-injector";
    ConcurrentSpaceInjector spaceInjector = new ConcurrentSpaceInjector();
    ExecutorService executor = Executors.newFixedThreadPool(spaceInjector.getThreadPoolSize());
    spaceInjector.login("root", "gtn");
    for (int i = 0; i < spaceInjector.getThreadPoolSize(); i++)
    {
      Map<String,String> params = new HashMap<>();
      params.put("spaceCreator", userPrefix + i);
      params.put("spaceName", spacePrefix + i);
      params.put("userPrefix", userPrefix);
      params.put("spacePrefix", spacePrefix);

      SpaceCreator worker = new SpaceCreator(spaceInjector.getHttpClient(),
          spaceInjectionBaseURL,
          params);
      executor.execute(worker);
    }
    executor.shutdown();
    while (!executor.isTerminated())
    {
    }
    spaceInjector.releaseConnection();
    LOG.info("************* Space Injection is done *************");
  }

  public void login(String username, String password)
  {
    CloseableHttpResponse response = null;
    try {
      HttpPost httpPost = new HttpPost("http://localhost:8080/portal/login");
      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
      nameValuePairs.add(new BasicNameValuePair("username", username));
      nameValuePairs.add(new BasicNameValuePair("password", password));
      httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
      HttpClientContext context = HttpClientContext.create();
      response = httpClient.execute(httpPost, context);
    } catch (UnsupportedEncodingException uee) {
      LOG.error("Can not write login response: ", uee);
    } catch (ClientProtocolException cpe) {
      LOG.error("Error while login: ", cpe);
    } catch (IOException ioe) {
      LOG.error("Error while login: ", ioe);
    } finally {
      try
      {
        response.close();
      } catch (IOException e)
      {
        LOG.error("The connection cannot be closed", e);
      }
    }
  }

  public CloseableHttpClient getHttpClient()
  {
    return httpClient;
  }

  public int getThreadPoolSize()
  {
    return THREAD_POOL_SIZE;
  }

  private void releaseConnection()
  {
    try
    {
      httpClient.close();
    } catch (IOException e)
    {
      LOG.error("The connection is broken", e);
    }
  }
}
