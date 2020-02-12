/**
 * Copyright (c)  WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useState, useEffect, useContext } from 'react';
import PropTypes from 'prop-types';
import { Grid, TextField, MenuItem } from '@material-ui/core';
import { FormattedMessage, injectIntl } from 'react-intl';
import { isRestricted } from 'AppData/AuthManager';
import APIContext from 'AppComponents/Apis/Details/components/ApiContext';
import APIValidation from 'AppData/APIValidation';
import FormControl from '@material-ui/core/FormControl';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import FormLabel from '@material-ui/core/FormLabel';
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import { withStyles } from '@material-ui/core/styles';

const styles = () => ({
    FormControl: {
        padding: 0,
        width: '100%',
    },
    radioWrapper: {
        display: 'flex',
        flexDirection: 'row',
    },
});

/**
 * The base component for advanced endpoint configurations.
 * @param {any} props The props that are being passed
 * @returns {any} The html representation of the component.
 */
function EndpointSecurity(props) {
    const { api } = useContext(APIContext);
    const {
        intl, securityInfo, onChangeEndpointAuth, classes,
    } = props;
    const [endpointSecurityInfo, setEndpointSecurityInfo] = useState({
        type: 'BASIC',
        username: '',
        password: '',
        tokenUrl: '',
        httpMethod: 'post',
        apiKey: '',
        apiSecret: '',
        grantType: '',
    });
    const [securityValidity, setSecurityValidity] = useState();

    const authTypes = [
        {
            key: 'BASIC',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.basic',
                defaultMessage: 'Basic Auth',
            }),
        },
        {
            key: 'DIGEST',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.digest.auth',
                defaultMessage: 'Digest Auth',
            }),
        },
        {
            key: 'OAUTH',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.oauth',
                defaultMessage: 'OAuth',
            }),
        },
    ];

    const grantTypes = [
        {
            key: 'CLIENT_CREDENTIALS',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.oauth.grant.type.client',
                defaultMessage: 'Client Credentials',
            }),
        },
        {
            key: 'PASSWORD',
            value: intl.formatMessage({
                id: 'APis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.oauth.grant.type.password',
                defaultMessage: 'Password',
            }),
        },
    ];
    useEffect(() => {
        const tmpSecurity = {};
        if (securityInfo !== null) {
            const {
                type, username, password, grantType, httpMethod, tokenUrl, apiKey, apiSecret,
            } = securityInfo;
            tmpSecurity.type = type;
            tmpSecurity.username = username;
            tmpSecurity.password = password === '' ? '**********' : password;
            tmpSecurity.grantType = grantType;
            tmpSecurity.httpMethod = httpMethod;
            tmpSecurity.tokenUrl = tokenUrl;
            tmpSecurity.apiKey = apiKey;
            tmpSecurity.apiSecret = apiSecret;
        }
        setEndpointSecurityInfo(tmpSecurity);
    }, [props]);

    const validateTokenURL = (value) => {
        const state = APIValidation.url.required().validate(value).error;
        // state 'null' means the URL is valid.
        if (state === null) {
            return true;
        } else {
            return false;
        }
    };

    const validateAndUpdateSecurityInfo = (field) => {
        if (!endpointSecurityInfo[field]) {
            setSecurityValidity({ ...securityValidity, [field]: false });
        } else {
            let validity = true;
            if (field === 'tokenUrl') {
                validity = validateTokenURL(endpointSecurityInfo[field]);
            }
            setSecurityValidity({ ...securityValidity, [field]: validity });
        }
        onChangeEndpointAuth(endpointSecurityInfo[field], field);
    };
    return (
        <Grid container direction='row' spacing={2}>
            <Grid item xs={6}>
                <TextField
                    disabled={isRestricted(['apim:api_create'], api)}
                    fullWidth
                    select
                    value={endpointSecurityInfo.type}
                    variant='outlined'
                    onChange={(event) => {
                        onChangeEndpointAuth(event.target.value, 'type');
                    }}
                    inputProps={{
                        name: 'key',
                        id: 'auth-type-select',
                    }}
                >
                    {authTypes.map((type) => (
                        <MenuItem value={type.key}>{type.value}</MenuItem>
                    ))}
                </TextField>
            </Grid>
            <Grid item xs={6} />

            {(endpointSecurityInfo.type === 'OAUTH')
            && (
                <>
                    <Grid item xs={6}>
                        <TextField
                            disabled={isRestricted(['apim:api_create'], api)}
                            required
                            fullWidth
                            select
                            label={(
                                <FormattedMessage
                                    id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.grant.type.input'
                                    defaultMessage='Grant Type'
                                />
                            )}
                            variant='outlined'
                            onChange={(event) => setEndpointSecurityInfo(
                                { ...endpointSecurityInfo, grantType: event.target.value },
                            )}
                            value={endpointSecurityInfo.grantType}
                            inputProps={{
                                name: 'key',
                                id: 'grant-type-select',
                            }}
                            onBlur={() => validateAndUpdateSecurityInfo('grantType')}
                        >
                            {grantTypes.map((type) => (
                                <MenuItem value={type.key}>{type.value}</MenuItem>
                            ))}
                        </TextField>
                    </Grid>


                    {(endpointSecurityInfo.grantType === 'CLIENT_CREDENTIALS'
                    || endpointSecurityInfo.grantType === 'PASSWORD') && (
                        <>
                            <Grid item xs={6}>
                                {/* <TextField
                                    disabled={isRestricted(['apim:api_create'], api)}
                                    required
                                    fullWidth
                                    error={securityValidity && securityValidity.tokenUrl === false}
                                    helperText={
                                        securityValidity && securityValidity.tokenUrl === false ? (
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration'
                                        + '.EndpointSecurity.no.tokenUrl.error'}
                                                defaultMessage='Token URL should not be empty or formatted incorrectly'
                                            />
                                        ) : (
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.tokenUrl.message'}
                                                defaultMessage='Enter Token URL'
                                            />
                                        )
                                    }
                                    variant='outlined'
                                    id='auth-tokenUrl'
                                    label={(
                                        <FormattedMessage
                                            id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                            + 'EndpointSecurity.token.url.input'}
                                            defaultMessage='Token URL'
                                        />
                                    )}
                                    onChange={(event) => setEndpointSecurityInfo(
                                        { ...endpointSecurityInfo, tokenUrl: event.target.value },
                                    )}
                                    value={endpointSecurityInfo.tokenUrl}
                                    onBlur={() => validateAndUpdateSecurityInfo('tokenUrl')}
                                /> */}
                                <FormControl className={classes.FormControl}>
                                    <FormLabel component='legend'>HTTP Method: </FormLabel>
                                    <RadioGroup
                                        aria-label='HTTP Method'
                                        name='httpMethod'
                                        className={classes.radioWrapper}
                                        value={endpointSecurityInfo.httpMethod}
                                        onChange={(event) => setEndpointSecurityInfo(
                                            { ...endpointSecurityInfo, httpMethod: event.target.value },
                                        )}
                                        onBlur={() => validateAndUpdateSecurityInfo('httpMethod')}
                                    >
                                        <FormControlLabel
                                            control={<Radio color='primary' />}
                                            value='get'
                                            label={(
                                                <FormattedMessage
                                                    id={'Apis.Details.Endpoints.GeneralConfiguration'
                                                    + '.EndpointSecurity.Get'}
                                                    defaultMessage='GET'
                                                />
                                            )}
                                            className={classes.radioGroup}
                                        />
                                        <FormControlLabel
                                            control={(<Radio color='primary' />)}
                                            value='post'
                                            label={(
                                                <FormattedMessage
                                                    id={'Apis.Details.Endpoints.GeneralConfiguration'
                                                    + '.EndpointSecurity.Post'}
                                                    defaultMessage='POST'
                                                />
                                            )}
                                            className={classes.radioGroup}
                                            defaultChecked
                                        />
                                    </RadioGroup>
                                </FormControl>
                            </Grid>

                            <Grid item xs={6}>
                                <TextField
                                    disabled={isRestricted(['apim:api_create'], api)}
                                    required
                                    fullWidth
                                    error={securityValidity && securityValidity.tokenUrl === false}
                                    helperText={
                                        securityValidity && securityValidity.tokenUrl === false ? (
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration'
                                        + '.EndpointSecurity.no.tokenUrl.error'}
                                                defaultMessage='Token URL should not be empty or formatted incorrectly'
                                            />
                                        ) : (
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.tokenUrl.message'}
                                                defaultMessage='Enter Token URL'
                                            />
                                        )
                                    }
                                    variant='outlined'
                                    id='auth-tokenUrl'
                                    label={(
                                        <FormattedMessage
                                            id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                            + 'EndpointSecurity.token.url.input'}
                                            defaultMessage='Token URL'
                                        />
                                    )}
                                    onChange={(event) => setEndpointSecurityInfo(
                                        { ...endpointSecurityInfo, tokenUrl: event.target.value },
                                    )}
                                    value={endpointSecurityInfo.tokenUrl}
                                    onBlur={() => validateAndUpdateSecurityInfo('tokenUrl')}
                                />
                            </Grid>

                            <Grid item xs={6}>
                                <TextField
                                    disabled={isRestricted(['apim:api_create'], api)}
                                    required
                                    fullWidth
                                    error={securityValidity && securityValidity.apiKey === false}
                                    helperText={
                                        securityValidity && securityValidity.apiKey === false ? (
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.no.apiKey.error'}
                                                defaultMessage='API Key should not be empty'
                                            />
                                        ) : (
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                                + 'EndpointSecurity.apiKey.message'}
                                                defaultMessage='Enter API Key'
                                            />
                                        )
                                    }
                                    variant='outlined'
                                    id='auth-apiKey'
                                    label={(
                                        <FormattedMessage
                                            id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                            + 'EndpointSecurity.api.key.input'}
                                            defaultMessage='API Key'
                                        />
                                    )}
                                    onChange={(event) => setEndpointSecurityInfo(
                                        { ...endpointSecurityInfo, apiKey: event.target.value },
                                    )}
                                    value={endpointSecurityInfo.apiKey}
                                    onBlur={() => validateAndUpdateSecurityInfo('apiKey')}
                                />
                            </Grid>

                            <Grid item xs={6}>
                                <TextField
                                    disabled={isRestricted(['apim:api_create'], api)}
                                    required
                                    fullWidth
                                    error={securityValidity && securityValidity.apiSecret === false}
                                    helperText={
                                        securityValidity && securityValidity.apiSecret === false ? (
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.no.apiSecret.error'}
                                                defaultMessage='API Secret should not be empty'
                                            />
                                        ) : (
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.apiSecret.message'}
                                                defaultMessage='Enter API Secret'
                                            />
                                        )
                                    }
                                    variant='outlined'
                                    id='auth-apiSecret'
                                    label={(
                                        <FormattedMessage
                                            id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                            + 'EndpointSecurity.api.secret.input'}
                                            defaultMessage='API Secret'
                                        />
                                    )}
                                    onChange={(event) => setEndpointSecurityInfo(
                                        { ...endpointSecurityInfo, apiSecret: event.target.value },
                                    )}
                                    value={endpointSecurityInfo.apiSecret}
                                    onBlur={() => validateAndUpdateSecurityInfo('apiSecret')}
                                />
                            </Grid>
                        </>
                    )}
                </>
            )}

            {(endpointSecurityInfo.type === 'BASIC'
            || endpointSecurityInfo.type === 'DIGEST'
            || endpointSecurityInfo.grantType === 'PASSWORD') && (
                <>
                    <Grid item xs={6}>
                        <TextField
                            disabled={isRestricted(['apim:api_create'], api)}
                            required
                            fullWidth
                            error={securityValidity && securityValidity.username === false}
                            helperText={
                                securityValidity && securityValidity.username === false ? (
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.no.username.error'}
                                        defaultMessage='Username should not be empty'
                                    />
                                ) : (
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.username.message'}
                                        defaultMessage='Enter Username'
                                    />
                                )
                            }
                            variant='outlined'
                            id='auth-userName'
                            label={(
                                <FormattedMessage
                                    id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.user.name.input'
                                    defaultMessage='Username'
                                />
                            )}
                            onChange={(event) => setEndpointSecurityInfo(
                                { ...endpointSecurityInfo, username: event.target.value },
                            )}
                            value={endpointSecurityInfo.username}
                            onBlur={() => validateAndUpdateSecurityInfo('username')}
                        />
                    </Grid>

                    <Grid item xs={6}>
                        <TextField
                            disabled={isRestricted(['apim:api_create'], api)}
                            required
                            fullWidth
                            error={securityValidity && securityValidity.password === false}
                            helperText={
                                securityValidity && securityValidity.password === false ? (
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.no.password.error'}
                                        defaultMessage='Password should not be empty'
                                    />
                                ) : (
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.password.message'}
                                        defaultMessage='Enter Password'
                                    />
                                )
                            }
                            variant='outlined'
                            type='password'
                            id='auth-password'
                            label={(
                                <FormattedMessage
                                    id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.password.input'
                                    defaultMessage='Password'
                                />
                            )}
                            value={endpointSecurityInfo.password}
                            onChange={(event) => setEndpointSecurityInfo(
                                { ...endpointSecurityInfo, password: event.target.value },
                            )}
                            onBlur={() => validateAndUpdateSecurityInfo('password')}
                        />
                    </Grid>
                </>
            )}
        </Grid>
    );
}

EndpointSecurity.propTypes = {
    intl: PropTypes.func.isRequired,
    securityInfo: PropTypes.shape({}).isRequired,
    onChangeEndpointAuth: PropTypes.func.isRequired,
};

export default withStyles(styles)(injectIntl(EndpointSecurity));
