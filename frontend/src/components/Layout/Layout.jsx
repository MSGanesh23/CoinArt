import { Outlet } from 'react-router-dom'
import NavBar from '../NavBar/NavBar'
import SideNav from '../SideNav/SideNav'
import './Layout.css'

export default function Layout() {
    return (
        <div className="app-layout">
            <NavBar />
            <div className="app-body">
                <SideNav />
                <main className="main-content">
                    <Outlet />
                </main>
            </div>
        </div>
    )
}
