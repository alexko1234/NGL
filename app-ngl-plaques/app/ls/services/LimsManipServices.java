package ls.services;


import java.util.List;

import ls.dao.LimsManipDAO;
import ls.models.Manip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LimsManipServices {


        @Autowired
        LimsManipDAO dao;

        public List<Manip> getManips(Integer emnco, Integer ematerielco,String prsco) {
                return dao.getManips(emnco,ematerielco,prsco);
        }

}



