package com.soen343.JDBImappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.soen343.core.QueueNode;
import com.soen343.core.QueueNodeEdge;

public class QueueNodeEdgeJDBIMapper implements ResultSetMapper<QueueNodeEdge> {

	@Override
	public QueueNodeEdge map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		 return new QueueNodeEdge(r.getLong("id"), r.getLong("parent_id"), r.getLong("child_id"));
	}

}