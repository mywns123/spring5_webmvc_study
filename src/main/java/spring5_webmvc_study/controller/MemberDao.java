package spring5_webmvc_study.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class MemberDao {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public List<Member> selectByRegdate(LocalDateTime from, LocalDateTime to){
		String sql = "select * from `member` where regdate between ? and ? order by regdate desc";
		return jdbcTemplate.query(sql, memberRowMapper ,from, to );
	}
	

	/* 결과가 1개 이상인 경우 */
	public Member selectByEmail(String email) {
		List<Member> result = jdbcTemplate.query("select * from `member` where email = ?", new MemberRowMapper(),
				email);
		return result.isEmpty() ? null : result.get(0);
	}

	public Member selectbyId(long memId) {
		String sql = "select * from `member` where id = ?";
		List<Member> result = jdbcTemplate.query(sql, memberRowMapper, memId);
		return result.isEmpty()? null:result.get(0);		
	}
	
	public List<Member> selectAll() {
		return jdbcTemplate.query("select * from `member`", new MemberRowMapper());
	}

	/* 결과과 1행인 경우 */
	public int count() {
		return jdbcTemplate.queryForObject("select count(*) from `member`", Integer.class);
	}

	public void insert(Member member) {
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(
						"insert into `member`(email, password, name, regdate) values(?,?,?,?)", new String[] { "id" });
				pstmt.setString(1, member.getEmail());
				pstmt.setString(2, member.getPassword());
				pstmt.setString(3, member.getName());
				pstmt.setTimestamp(4, Timestamp.valueOf(member.getRegisterDateTime()));
				return pstmt;
			}
		};

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(psc, keyHolder);
		Number keyValue = keyHolder.getKey();
		member.setId(keyValue.longValue());

	}

	public void update(Member member) {
		jdbcTemplate.update("update `member` set password = ?, name = ? where email = ?",
				member.getPassword(), member.getName(), member.getEmail());
	}

	public void delete(Member member) {
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pstmt = con.prepareStatement("delete from `member` where email = ?");
				pstmt.setString(1, member.getEmail());
				return pstmt;
			}
		};
		jdbcTemplate.update(psc);
	}

	private RowMapper<Member> memberRowMapper = new RowMapper<Member>(){
		@Override
		public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
			Member member = new Member(rs.getString("email"),
					rs.getString("password"),
					rs.getString("name"),
					rs.getTimestamp("regdate").toLocalDateTime()
					);
			member.setId(rs.getLong("id"));
			return member;
		}
	};
	
}



