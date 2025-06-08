package repositories

import java.sql.Connection
import javax.inject.Inject
import models.User
import play.api.db.Database

import scala.util.Using

class UserRepository @Inject()(db: Database) {

    def list(): Seq[User] = {
        Using.resource(db.getConnection()) { conn =>
            val stmt = conn.prepareStatement("SELECT id, name, email FROM users")
            val rs = stmt.executeQuery()
            Iterator.continually((rs, rs.next()))
                .takeWhile(_._2)
                .map { case (r, _) =>
                    User(Some(r.getLong("id")), r.getString("name"), r.getString("email"))
                }.toList
        }
    }

    def create(user: User): Option[Long] = {
        Using.resource(db.getConnection()) { conn =>
            val stmt = conn.prepareStatement("INSERT INTO users (name, email) VALUES (?, ?) RETURNING id")
            stmt.setString(1, user.name)
            stmt.setString(2, user.email)
            val rs = stmt.executeQuery()
            if (rs.next()) Some(rs.getLong(1)) else None
        }
    }

    def get(id: Long): Option[User] = {
        Using.resource(db.getConnection()) { conn =>
            val stmt = conn.prepareStatement("SELECT id, name, email FROM users WHERE id = ?")
            stmt.setLong(1, id)
            val rs = stmt.executeQuery()
            if (rs.next())
                Some(User(Some(rs.getLong("id")), rs.getString("name"), rs.getString("email")))
            else None
        }
    }

    def update(id: Long, user: User): Boolean = {
        Using.resource(db.getConnection()) { conn =>
            val stmt = conn.prepareStatement("UPDATE users SET name = ?, email = ? WHERE id = ?")
            stmt.setString(1, user.name)
            stmt.setString(2, user.email)
            stmt.setLong(3, id)
            stmt.executeUpdate() > 0
        }
    }

    def delete(id: Long): Boolean = {
        Using.resource(db.getConnection()) { conn =>
            val stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")
            stmt.setLong(1, id)
            stmt.executeUpdate() > 0
        }
    }
}