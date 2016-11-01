/*
 * DaRPC: Data Center Remote Procedure Call
 *
 * Author: Patrick Stuedi <stu@zurich.ibm.com>
 *
 * Copyright (C) 2016, IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ibm.darpc.examples.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.ibm.darpc.RpcServerEndpoint;
import com.ibm.darpc.RpcServerGroup;
import com.ibm.darpc.examples.protocol.RdmaRpcRequest;
import com.ibm.darpc.examples.protocol.RdmaRpcResponse;
import com.ibm.disni.rdma.*;
import com.ibm.disni.util.*;

public class DaRPCServer {
	private String ipAddress; 
	private int poolsize = 3;
	private int rpcpipeline = 100;
	private int servicetimeout = 0;
	private boolean polling = false;
	private int maxinline = 0;
	
	public void run() throws Exception{
		System.out.println("RpcServer::run: serverIP=" + ipAddress + ", poolsize " + poolsize);
		InetAddress localHost = InetAddress.getByName(ipAddress);
		InetSocketAddress addr = new InetSocketAddress(localHost, 1919);	
		
		long[] clusterAffinities;
		if (poolsize == 1){
			long _clusterAffinities[] = { 1L << 1 | 1L << 17};
			clusterAffinities = _clusterAffinities;
		} else if (poolsize == 2){
			long _clusterAffinities[] = { 1L << 1 | 1L << 17, 1L << 2 | 1L << 18, 1L << 3 | 1L << 19, 1L << 4 | 1L << 20};
			clusterAffinities = _clusterAffinities;
		} else if (poolsize == 3){
			long _clusterAffinities[] = { 1L << 9 | 1L << 25, 1L << 10 | 1L << 26, 1L << 11 | 1L << 27, 1L << 12 | 1L << 28};
			clusterAffinities = _clusterAffinities;
		} else if (poolsize == 4){
			long _clusterAffinities[] = {1L << 1 | 1L << 17, 1L << 2 | 1L << 18, 1L << 3 | 1L << 19, 1L << 4 | 1L << 20, 1L << 9 | 1L << 25, 1L << 10 | 1L << 26, 1L << 11 | 1L << 27, 1L << 12 | 1L << 28};
			clusterAffinities = _clusterAffinities;
		} else if (poolsize == 5){
			long _clusterAffinities[] = { 1L << 1 | 1L << 17, 1L << 2 | 1L << 18, 1L << 3 | 1L << 19, 1L << 4 | 1L << 20};
			clusterAffinities = _clusterAffinities;
		} else if (poolsize == 6){
			System.out.println("Animesh layout");
			long _clusterAffinities[] = { 1L << 0 | 1L << 16, 1L << 8 | 1L << 24, 1L << 4 | 1L << 20, 1L << 12 | 1L << 28};
			clusterAffinities = _clusterAffinities;			
		} else if (poolsize == 11){
			long _clusterAffinities[] = { 1L << 1 | 1L << 17};
			clusterAffinities = _clusterAffinities;			
		} else if (poolsize == 12){
			long _clusterAffinities[] = { 1L << 1 | 1L << 17, 1L << 2 | 1L << 18};
			clusterAffinities = _clusterAffinities;			
		} else if (poolsize == 13){
			long _clusterAffinities[] = { 1L << 1 | 1L << 17, 1L << 2 | 1L << 18, 1L << 3 | 1L << 19};
			clusterAffinities = _clusterAffinities;			
		} else if (poolsize == 14){
			long _clusterAffinities[] = { 1L << 1 | 1L << 17, 1L << 2 | 1L << 18, 1L << 3 | 1L << 19, 1L << 4 | 1L << 20};
			clusterAffinities = _clusterAffinities;			
		} else if (poolsize == 15){
			long _clusterAffinities[] = { 1L << 1 | 1L << 17, 1L << 2 | 1L << 18, 1L << 3 | 1L << 19, 1L << 4 | 1L << 20, 1L << 9 | 1L << 25};
			clusterAffinities = _clusterAffinities;			
		} else if (poolsize == 16){
			long _clusterAffinities[] = { 1L << 1 | 1L << 17, 1L << 2 | 1L << 18, 1L << 3 | 1L << 19, 1L << 4 | 1L << 20, 1L << 9 | 1L << 25, 1L << 10 | 1L << 26};
			clusterAffinities = _clusterAffinities;			
		} else if (poolsize == 17){
			long _clusterAffinities[] = {1L << 1 | 1L << 17, 1L << 2 | 1L << 18, 1L << 3 | 1L << 19, 1L << 4 | 1L << 20, 1L << 9 | 1L << 25, 1L << 10 | 1L << 26, 1L << 11 | 1L << 27};
			clusterAffinities = _clusterAffinities;			
		} else if (poolsize == 18){
			long _clusterAffinities[] = {1L << 1 | 1L << 17, 1L << 2 | 1L << 18, 1L << 3 | 1L << 19, 1L << 4 | 1L << 20, 1L << 9 | 1L << 25, 1L << 10 | 1L << 26, 1L << 11 | 1L << 27, 1L << 12 | 1L << 28};
			clusterAffinities = _clusterAffinities;			
		} else {
			long _clusterAffinities[] = { 1L << 1 | 1L << 17 };
			clusterAffinities = _clusterAffinities;
		}		

		System.out.println("poolsize " + poolsize + ", affinity size " + clusterAffinities.length);
		RdmaRpcService rpcService = new RdmaRpcService(servicetimeout);
		RpcServerGroup<RdmaRpcRequest, RdmaRpcResponse> group = RpcServerGroup.createServerGroup(rpcService, clusterAffinities, -1, maxinline, polling, rpcpipeline, 4, rpcpipeline); 
		RdmaServerEndpoint<RpcServerEndpoint<RdmaRpcRequest, RdmaRpcResponse>> serverEp = group.createServerEndpoint();
		serverEp.bind(addr, 1000);
		System.out.println("Opened rdma server at " + addr);

		while(true){
			serverEp.accept();
		}		
	}
	
	public void launch(String[] args) throws Exception {
		String[] _args = args;
		if (args.length < 1) {
			System.exit(0);
		} else if (args[0].equals(DaRPCServer.class.getCanonicalName())) {
			_args = new String[args.length - 1];
			for (int i = 0; i < _args.length; i++) {
				_args[i] = args[i + 1];
			}
		}

		GetOpt go = new GetOpt(_args, "a:s:r:p:di:");
		go.optErr = true;
		int ch = -1;

		System.setProperty("com.ibm.jverbs.provider", "nat");
		System.setProperty("com.ibm.jverbs.driver", "siw");

		while ((ch = go.getopt()) != GetOpt.optEOF) {
			if ((char) ch == 'a') {
				ipAddress = go.optArgGet();
			} else if ((char) ch == 's') {
				int serialized_size = Integer.parseInt(go.optArgGet());
				RdmaRpcRequest.SERIALIZED_SIZE = serialized_size;
				RdmaRpcResponse.SERIALIZED_SIZE = serialized_size;
			} else if ((char) ch == 'p') {
				poolsize = Integer.parseInt(go.optArgGet());
			} else if ((char) ch == 'r') {
				rpcpipeline = Integer.parseInt(go.optArgGet());
			} else if ((char) ch == 't') {
				servicetimeout = Integer.parseInt(go.optArgGet());
			} else if ((char) ch == 'd') {
				polling = true;
			} else if ((char) ch == 'i') {
				maxinline = Integer.parseInt(go.optArgGet());
			} else {
				System.exit(1); // undefined option
			}
		}	
		
		this.run();
	}
	
	public static void main(String[] args) throws Exception { 
		DaRPCServer rpcServer = new DaRPCServer();
		rpcServer.launch(args);		
	}	
}
