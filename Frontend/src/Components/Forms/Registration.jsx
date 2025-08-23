import { useState,useEffect } from 'react';
import './Forms.css'
import { Navigate, NavLink, useNavigate } from 'react-router-dom';
function Registration(){

    
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    const navigate = useNavigate();

    const handleChangeName = (event) => {
        setName(event.target.value)
    }

    const handleChangeEmail = (event) => {
        setEmail(event.target.value)
        
    }

    const handleChangePassword = (event) => {
        setPassword(event.target.value)
    } 



    const handleSubmit = async (e) => {
      e.preventDefault();

      try {
        const response = await fetch(`http://localhost:8080/auth/signup`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
              username:name,
              email:email,
              password:password,
          }),
          // mode: 'cors'
        });

        if (response.ok) {
          navigate('/login')
        } else {
          alert('Ошибка при отправке данных');
        }
      } catch (error) {
        console.error('Ошибка:', error);
      }
    };


    return(
        <div className="forms_block">
            <form className="forms_register" onSubmit={handleSubmit}>
                <div className="main_form_text">Регистрация</div>
                <div className="form_block">
                    <label className="form_label">Имя</label>
                    <input className="form_input" value={name} onChange={handleChangeName}/>
                </div>
                <div className="form_block">
                    <label className="form_label">Пароль</label>
                    <input className="form_input" value={password} onChange={handleChangePassword}/>
                </div> 
                <div className="form_block">
                    <label className="form_label">Почта</label>
                    <input className="form_input" value={email} type="email" onChange={handleChangeEmail}/>
                </div>
                
                <button className="form_btn" type='submit'>Создать</button>
                <div className="little_form_text"><NavLink to = "/login">Войти</NavLink></div>
            </form>
        </div>
    )
}

export default Registration;