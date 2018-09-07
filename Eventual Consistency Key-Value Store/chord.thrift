
exception SystemException {
  1: optional string message
}

struct NodeID {
  1: string ip;
  2: i32 port;
}

struct DataValue {
 1: string timestamp;
 2: string value;
}

service ReplicaServices {
  void WriteData(1: i32 key, 2: string value, 3: i32 consistencyLevel)
    throws (1: SystemException systemException),
  
  string UpdateReplicas(1: i32 key, 2: string value, 3: string ts)
    throws (1: SystemException systemException),

  string ReadData(1: i32 key, 2: i32 consistencyLevel)
    throws (1: SystemException systemException),

  void ReadRepair(1: i32 key)
    throws (1: SystemException systemException),
  
  void Hinted_handoff(1: i32 key, 2: string value)
    throws (1: SystemException systemException),
  
  DataValue GetDataValue(1: i32 key)
    throws (1: SystemException systemException),

  void setFingertable(1: list<NodeID> node_list),
}
