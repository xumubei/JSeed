package com.jseed.core.starter.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;

/**
 * Snowflake ID generator
 *
 * @author cyrus
 * @date: 2024-07-10
 */
public class SnowFlakeIdGenerator {

    /**
     * Custom epoch. Choose a timestamp such as the project's creation date.
     */
    private final static long START_STMP = 1543903501000L;

    /**
     * Bit width for each component
     */
    /**
     * Sequence bits
     */
    private final static long SEQUENCE_BIT = 12;
    /**
     * Worker ID bits
     */
    private final static long MACHINE_BIT = 5;
    /**
     * Data center ID bits
     */
    private final static long DATACENTER_BIT = 5;

    /**
     * Calculate each maximum by XORing -1 with -1 shifted left by the bit width.
     */
    /**
     * Maximum data center ID for five bits: 31.
     */
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);

    /**
     * Maximum worker ID for five bits: 31.
     */
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);

    /**
     * Largest positive value for 12 bits: 4095.
     */
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /*
     * Left shift for each component
     */

    /**
     * Worker ID offset after the sequence
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;

    /**
     * Data center ID offset after the worker ID
     */
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    /**
     * Timestamp offset after the data center ID
     */
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
    /**
     * Data center ID
     */
    private static long datacenterId;
    /**
     * Worker ID
     */
    private static long machineId;
    /**
     * Sequence number
     */
    private static long sequence = 0L;
    /**
     * Previous timestamp
     */
    private static long lastStmp = -1L;

    /**
     * The private no-argument constructor and the absence of a parameterized constructor address two issues:
     * 1. Reusing one instance prevents repeated construction in a loop from resetting lastStmp and potentially generating duplicate IDs.
     * 2. Fixed parameters help generators in the same distributed system use a consistent ID layout.
     */
    private SnowFlakeIdGenerator() {
    }

    public static String nextStrId() {
        return nextId() + "";
    }

    /**
     * Generates the next ID.
     *
     * @return
     */
    public static synchronized long nextId() {
        /* Get the current timestamp. */
        long currStmp = getNewstmp();

        /* Throw an exception if the clock moved backward. */
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }
        /* Within the same millisecond */
        if (currStmp == lastStmp) {
            //Increment the sequence within the same millisecond.
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //The sequence reached its maximum for this millisecond.
            if (sequence == 0L) {

                /* Wait for the next millisecond and use that timestamp. */
                currStmp = getNextMill();
            }
        } else {
            //Reset the sequence to zero for a new millisecond.
            sequence = 0L;
        }
        /* Save the timestamp to compare against the next ID request. */
        lastStmp = currStmp;

        //Timestamp bits + data center bits + worker ID bits + sequence bits.
        return (currStmp - START_STMP) << TIMESTMP_LEFT
                | datacenterId << DATACENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    private static long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private static long getNewstmp() {
        return System.currentTimeMillis();
    }

    /**
     * Get the local IP address.
     *
     * @return
     */
    private static String getHostIp() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //The IPv4 loopback range is 127.0.0.0 through 127.255.255.255.
                            && !ip.getHostAddress().contains(":")) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /*Default ID assignment:
    Derive the worker ID by converting the server IP to a long and taking it modulo 32.
    Consecutive server IPs such as 192.168.180.54 through .58 yield distinct results. Nonconsecutive IPs can collide.
    Choose a random data center ID from 0 through 31.*/
    static {
        Random rd = new Random();
        long workerId = rd.nextInt(31), centerId = rd.nextInt(31); //Worker ID and data center ID

        //Generate the worker ID from the local IP address.
        String ip = getHostIp();
        if (ip != null) {
            workerId = Long.parseLong(ip.replaceAll("\\.", ""));
            workerId = workerId % 32; //Use modulo 32 to fit the value into five bits.
        }
        datacenterId = centerId;
        machineId = workerId;
    }
}
