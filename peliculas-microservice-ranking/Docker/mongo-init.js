db.createUser(
    {
        user: 'capo',
        pwd: 'eyra',
        roles: [
            {
                role: 'root',
                db: 'admin'
            }
        ]
    }
);
