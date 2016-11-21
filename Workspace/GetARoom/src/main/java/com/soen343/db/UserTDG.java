package com.soen343.db;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.soen343.JDBImappers.UserJDBIMapper;
import com.soen343.core.User;

@RegisterMapper(UserJDBIMapper.class)
public interface UserTDG {
	
	@SqlUpdate("insert into User (description) values (:description)")
	void insert(@BindBean User user);

	@SqlUpdate("update User set description = :r.description")
	void update(@BindBean("r") User user);

	@SqlQuery("select * from User where id = :id")
	User findById(@Bind("id") long id);
	
	@SqlQuery("select * from User where username = :username")
	User findByUsername(@Bind("username") String username);

	@SqlQuery("select * from User")
	List<User> getAll();

	@SqlUpdate("delete from User where id = :it")
	void deleteById(@Bind long id);
}
