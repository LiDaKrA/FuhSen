package de.ddb.common.filter

import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

/**
 * Sample filter implementation to define a set of Content Security Policies.<br/>
 *
 * This implementation has a dependency on Commons Codec API.<br/>
 *
 * This filter set CSP policies using all HTTP headers defined into W3C specification.<br/>
 * <br/>
 * This implementation is oriented to be easily understandable and easily adapted.<br/>
 *
 */
public class CSPPoliciesApplier {

    private static final String KEYWORD_NONE = "'none'"
    private static final String KEYWORD_SELF = "'self'"
    private static final String KEYWORD_UNSAFE_EVAL = "'unsafe-eval'"
    private static final String KEYWORD_UNSAFE_INLINE = "'unsafe-inline'"

    private static final URL_GOOGLE = "https://*.google.com"
    private static final URL_GOOGLE_PLUS_ONE = "https://plusone.google.com"
    private static final URL_GOOGLE_ACCOUNTS = "https://accounts.google.com"
    private static final URL_TWITTER_PLATFORM= "https://platform.twitter.com"
    private static final URL_FACEBOOK = "https://*.facebook.com"
    private static final URL_DDB = "*.deutsche-digitale-bibliothek.de"
    private static final URL_JW = "*.jwpcdn.com"
    private static final URL_GSTATIC = "*.gstatic.com"
    private static final URL_GOOGLE_APIS = "*.googleapis.com"

    private static final URL_VIMEO = "*.vimeo.com"

    /** List CSP HTTP Headers */
    private final List<String> cspHeaders = new ArrayList<String>()

    /** Collection of CSP polcies that will be applied */
    private final String policies

    public CSPPoliciesApplier() {
        // Define list of CSP HTTP Headers
        cspHeaders.add("Content-Security-Policy")
        cspHeaders.add("X-Content-Security-Policy")
        cspHeaders.add("X-WebKit-CSP")

        // Define CSP policies
        // Loading policies for Frame and Sandboxing will be dynamically defined : We need to know if context use Frame
        List<String> cspPolicies = new ArrayList<String>()
        // --Disable default source in order to avoid browser fallback loading using 'default-src' locations
        cspPolicies.add("default-src " + KEYWORD_NONE)
        // --Define loading policies for Scripts
        cspPolicies.add("script-src " + KEYWORD_SELF + " " + URL_DDB + " " + URL_GOOGLE + " " + URL_TWITTER_PLATFORM + " " + URL_FACEBOOK + " " + URL_JW + " " + URL_GSTATIC + " " + URL_GOOGLE_APIS + " " + KEYWORD_UNSAFE_INLINE + " " + KEYWORD_UNSAFE_EVAL)
        // --Define loading policies for Plugins
        cspPolicies.add("object-src " + KEYWORD_SELF)
        // --Define loading policies for Styles (CSS)
        cspPolicies.add("style-src " + URL_GOOGLE_APIS + " " + KEYWORD_SELF + " " + KEYWORD_UNSAFE_INLINE)
        // --Define loading policies for Images
        cspPolicies.add("img-src *")
        // --Define loading policies for Audios/Videos
        // DDBNEXT-1801: we add all hosts for media-src, because vimeo will use redirect to third-party domains to load its videos.
        cspPolicies.add("media-src *")
        // --Define loading policies for Frames
        cspPolicies.add("font-src " + URL_GSTATIC + " " + URL_GOOGLE_APIS + " " + KEYWORD_SELF)
        // --Define loading policies for Connection
        cspPolicies.add("connect-src " + URL_VIMEO + " " + KEYWORD_SELF)
        // --Define loading policies for Frames
        cspPolicies.add("frame-src " + KEYWORD_SELF + " " + URL_DDB + " " + URL_GOOGLE_PLUS_ONE + " " + URL_GOOGLE_ACCOUNTS + " "+ URL_GOOGLE + " " + URL_TWITTER_PLATFORM + " " + URL_FACEBOOK)

        // Target formatting
        policies = cspPolicies.toString().replaceAll("(\\[|\\])", "").replaceAll(",", ";").trim()
    }

    /**
     * Add CSP policies on each HTTP response.
     *
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void applyPolicies(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        /* Step 1 : Detect if target resource is a Frame */
        // TODO: Customize here according to your context...
        boolean isFrame = false

        /* Step 2 : Add CSP policies to HTTP response */
        StringBuilder policiesBuffer = new StringBuilder(policies)

        // If resource is a frame add Frame/Sandbox CSP policy
        if (isFrame) {
            // Frame + Sandbox : Here sandbox allow nothing, customize sandbox options depending on your app....
            policiesBuffer.append(";").append("frame-src " + KEYWORD_SELF + ";sandbox")
        }

        // Add policies to all HTTP headers
        for (String header : cspHeaders) {
            response.setHeader(header, policiesBuffer.toString())
        }
    }
}