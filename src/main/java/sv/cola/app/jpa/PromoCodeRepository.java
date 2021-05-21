package sv.cola.app.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sv.cola.app.domain.db.PromoCode;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, String> {}
