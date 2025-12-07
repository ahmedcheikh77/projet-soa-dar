package com.dme.distributed.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * JNDI Service Discovery and Registry
 */
public class JndiServiceRegistry {

    private static final String JNDI_FACTORY = "com.sun.jndi.rmi.registry.RegistryContextFactory";
    
    public static void registerService(String serviceName, Object serviceImpl, String rmiUrl) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
            env.put(Context.PROVIDER_URL, rmiUrl);

            Context ctx = new InitialContext(env);
            ctx.rebind(serviceName, serviceImpl);
            
            System.out.println("Service registered: " + serviceName);
        } catch (NamingException e) {
            System.err.println("Error registering service: " + e.getMessage());
        }
    }

    public static Object lookupService(String serviceName, String rmiUrl) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
            env.put(Context.PROVIDER_URL, rmiUrl);

            Context ctx = new InitialContext(env);
            Object service = ctx.lookup(serviceName);
            
            System.out.println("Service found: " + serviceName);
            return service;
        } catch (NamingException e) {
            System.err.println("Error looking up service: " + e.getMessage());
            return null;
        }
    }

    public static void unregisterService(String serviceName, String rmiUrl) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
            env.put(Context.PROVIDER_URL, rmiUrl);

            Context ctx = new InitialContext(env);
            ctx.unbind(serviceName);
            
            System.out.println("Service unregistered: " + serviceName);
        } catch (NamingException e) {
            System.err.println("Error unregistering service: " + e.getMessage());
        }
    }
}
