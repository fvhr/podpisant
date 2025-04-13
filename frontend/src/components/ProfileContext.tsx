import React, { createContext, useContext, useEffect, useState } from 'react';
import { fetchProfile } from '../api/user';

interface ProfileProps {
  is_super_admin: boolean;
  // другие поля профиля
}

interface ProfileContextType {
  profile: ProfileProps | null;
  loading: boolean;
  error: Error | null;
}

const ProfileContext = createContext<ProfileContextType>({
  profile: null,
  loading: true,
  error: null,
});

export const ProfileProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [state, setState] = useState<ProfileContextType>({
    profile: null,
    loading: true,
    error: null,
  });

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const data = await fetchProfile();
        setState((prev) => ({ ...prev, profile: data, loading: false }));
      } catch (err) {
        setState((prev) => ({ ...prev, error: err as Error, loading: false }));
      }
    };

    loadProfile();
  }, []);

  return <ProfileContext.Provider value={state}>{children}</ProfileContext.Provider>;
};

export const useProfile = () => useContext(ProfileContext);
